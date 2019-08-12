package com.userserviceprovider.config;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class MyRandomLoadBalance extends AbstractLoadBalance {


    private final Random random = new Random();


    /**
     * 官方的负载均衡策略---随机负载均衡（带权重的随机算法）
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        /**
         * invokers 是服务提供者 暴露的服务地址链总集合，
         * url 定义了调用的url如协议、协议、参数等信息。
         * Invocation 是会话域对象，它持有调用过程中的变量，比如方法名，参数等。
         *
         */
        int length = invokers.size(); // Number of invokers 服务地址链总个数
        int totalWeight = 0; // The sum of weights 初始化权重
        boolean sameWeight = true; // Every invoker has the same weight? 权重是否相同 默认相同
        for (int i = 0; i < length; i++) {
            //循环获取每个地址链的权重(具体如何获取权重，下面代码会讲)
            int weight = getWeight(invokers.get(i), invocation);
            totalWeight += weight; // Sum 计算所有的权重合计
            if (sameWeight && i > 0
                    && weight != getWeight(invokers.get(i - 1), invocation)) {
                //每两个相邻的服务权重值做比较，如果不等就修改状态值 false
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            //服务存在权重值 且 权重值不全相等
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            //根据总权重值大小，得到一个总权重值范围内的随机数
            int offset = random.nextInt(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                //随机数 减去循环中的服务的权重数
                offset -= getWeight(invokers.get(i), invocation);
                if (offset < 0) {
                    //如果相减后的数小于0 ，则返回当前这个服务。（服务权重值较大的会优先满足这个条件，且一次循环结束一定会有返回值）
                    return invokers.get(i);
                }
            }
        }
        //如果所有的服务权重一致（没有设置或设置权重值一样）。则随机返回一个服务地址。
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(random.nextInt(length));
    }

    /**
     * 获取服务权重值。
     */
//    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
    //获取当前服务地址链的权重值，不存在的默认为100.
//        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
//        if (weight > 0) {
    //获取服务启动的事件。
//            long timestamp = invoker.getUrl().getParameter(Constants.REMOTE_TIMESTAMP_KEY, 0L);
//            if (timestamp > 0L) {
    //服务到现在一共启动了多久。
//                int uptime = (int) (System.currentTimeMillis() - timestamp);
    //服务设定的预热时间 默认为10分钟
//                int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, Constants.DEFAULT_WARMUP);
    //如果服务的运行时间还没有达到 服务设定的预热时长,那么需要重新计算权重weight（即需要降权）
//                if (uptime > 0 && uptime < warmup) {
//                    weight = calculateWarmupWeight(uptime, warmup, weight);
//                }
//            }
//        }
//        return weight;
//    }

    /**
     * 权重降权策略（运行时间想对越接近服务预热时间，权重值越高，反之越低）
     * 在预热时长范围内，预热越久，性能越好，对应承担更多的权重。
     * JVM重启后有一段预热过程，要运行一段时间，它的性能才能达到最佳状态；阿里JVM团队就针对这个缺陷进行了优化
     */

//    static int calculateWarmupWeight(int uptime, int warmup, int weight) {

    //ww=运行总时长/(服务预热时长/权重值) 预热时长和权重值不变，除法所得的结果不变。那么重新计算所得的权重取决于服务运行时长
    //结论：运行时长越长，权重值越大。（dubbo认为 服务运行的时长越久，约为稳定，对应权重值会高。是一种权重取值的策略）
    //50000（100000/5）=2.5
//        int ww = (int) ((float) uptime / ((float) warmup / (float) weight));
//        return ww < 1 ? 1 : (ww > weight ? weight : ww);
//    }
}
