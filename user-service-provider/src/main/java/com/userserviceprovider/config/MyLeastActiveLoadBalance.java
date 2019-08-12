package com.userserviceprovider.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcStatus;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

public class MyLeastActiveLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "leastactive";

    private final Random random = new Random();

    /**
     * 服务维护一个活跃数计数器。例如:0代表服务已处理，目前完空闲状态；1代表还在处理中，处理完会减1到0的状态，接受新的请求
     * 官方的负载均衡策略---最少活跃数负载均衡。
     * （活跃数越小，执行效率越高。dubbo会让活跃数最小的服务器去执行新进来的的请求，多个最小活跃的提供者会根据权重分配，权重较大的有较高概率被调用）
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        //总数量
        int length = invokers.size(); // Number of invokers invokers
        //最小活跃数
        int leastActive = -1; // The least active value of all invokers
        //相同的相同最小活跃的个数
        int leastCount = 0; // The number of invokers having the same least active value (leastActive)
        //相同最小活跃的下角标数组
        int[] leastIndexs = new int[length]; // The index of invokers having the same least active value (leastActive)
        //权重总和
        int totalWeight = 0; // The sum of weights
        //第一个权重值，用于比较权重值是否全相等
        int firstWeight = 0; // Initial value, used for comparision
        //权重值全相等状态
        boolean sameWeight = true; // Every invoker has the same weight value?
        for (int i = 0; i < length; i++) {
            Invoker<T> invoker = invokers.get(i);
            //当前活跃数
            int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive(); // Active number
            //权重值 默认为100
            int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT); // Weight
            //如果发现有比当前最小活跃还小的活跃数，则重置当前最小活跃数 数据
            if (leastActive == -1 || active < leastActive) { // Restart, when find a invoker having smaller least active value.
                //重置最小活跃数为当前active
                leastActive = active; // Record the current least active value
                //重置最小活跃数个数为1
                leastCount = 1; // Reset leastCount, count again based on current leastCount
                //重置最小活跃数下角标数组
                leastIndexs[0] = i; // Reset
                //重置总权重和为当前权重值
                totalWeight = weight; // Reset
                //重置要对比的第一个权重值
                firstWeight = weight; // Record the weight the first invoker
                //重置权重相等状态为true
                sameWeight = true; // Reset, every invoker has the same weight value?
            } else if (active == leastActive) { // If current invoker's active value equals with leaseActive, then accumulating.
                //**********【核心】***最小活跃数相等的情况**************
                //给相同的最小活跃数相等的数组赋值，记录当前invoker的下角标。相同最小活跃的个数增+1
                leastIndexs[leastCount++] = i; // Record index number of this invoker
                // +=统计 总权重值
                totalWeight += weight; // Add this invoker's weight to totalWeight.
                // If every invoker has the same weight?
                //当前权重值与上一个权重值不同，则修改权重值相等状态为false
                if (sameWeight && i > 0
                        && weight != firstWeight) {
                    sameWeight = false;
                }
            }
        }
        // assert(leastCount > 0)
        //如果只有一个最小活跃的invoker 则直接返回。
        if (leastCount == 1) {
            // If we got exactly one invoker having the least active value, return this invoker directly.
            return invokers.get(leastIndexs[0]);
        }
        if (!sameWeight && totalWeight > 0) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            //依据总权重值 选择一个随机数作为阀值 权重随机数。
            int offsetWeight = random.nextInt(totalWeight);
            // Return a invoker based on the random value.xun
            //循环出所有的最小活跃数 数组记录的 invokers下角标
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexs[i];
                //权重随机数减去循环中的invoker的权重值，如果小于0 则返回该invoker。
                //权重值较大的将有较大的概率被返回调用。【这里和随机法负载均衡的策略一致】
                offsetWeight -= getWeight(invokers.get(leastIndex), invocation);
                if (offsetWeight <= 0)
                    return invokers.get(leastIndex);
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        // 如果权重相同或权重为0则均等随机
        return invokers.get(leastIndexs[random.nextInt(leastCount)]);
    }
}
