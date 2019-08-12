package com.userserviceprovider.config;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.AtomicPositiveInteger;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * dubbo负载均衡 ---轮询算法策略
 * 1.权重值设置不同：带权重值的轮询，权重值越小，被轮询调用的几率越小。反之越大。
 * （每轮询被调用一次，权重值就递减1的原因。且只有权重大于0的服务才会被返回，那么权重值小的服务，权重递减小于0的几率很高）
 * 2.权重值相同：调用次数取模服务长度，顺序返回服务。
 */
public class MyRoundRobinLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "roundrobin";

    //声明一个ConcurrentMap sequences key为String（要调用的接口类路径+方法名）value为一个正整数，AtomicPositiveInteger是线程安全的。
    private final ConcurrentMap<String, AtomicPositiveInteger> sequences = new ConcurrentHashMap<String, AtomicPositiveInteger>();


    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        //要调用服务的 服务名+方法名
        String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
        //可以调用的服务集合总个数
        int length = invokers.size(); // Number of invokers
        //最大权重
        int maxWeight = 0; // The maximum weight
        //最小权重
        int minWeight = Integer.MAX_VALUE; // The minimum weight
        //声明一个LinkedHashMap 保存所有权重值>0的服务  key为：被调用的invoker value为:该invoker服务的权重值。
        final LinkedHashMap<Invoker<T>, IntegerWrapper> invokerToWeightMap = new LinkedHashMap<Invoker<T>, MyRoundRobinLoadBalance.IntegerWrapper>();
        int weightSum = 0;
        for (int i = 0; i < length; i++) {
            //获取每个Invoker设置的权重，没有设置的默认为100
            int weight = getWeight(invokers.get(i), invocation);
            maxWeight = Math.max(maxWeight, weight); // Choose the maximum weight 找出最大权重值
            minWeight = Math.min(minWeight, weight); // Choose the minimum weight 找出最小权重值
            if (weight > 0) {
                //invokerToWeightMap 赋值
                invokerToWeightMap.put(invokers.get(i), new MyRoundRobinLoadBalance.IntegerWrapper(weight));
                //合计权重值
                weightSum += weight;
            }
        }
        //去sequences中获取当前的调用总次数，AtomicPositiveInteger解决了并发问题。
        AtomicPositiveInteger sequence = sequences.get(key);
        if (sequence == null) {
            //如果sequences不存在key 则添加key:服务名+方法名 vlaue：正整数（初始为0）
            sequences.putIfAbsent(key, new AtomicPositiveInteger());
            //获取value值 默认为0
            sequence = sequences.get(key);
        }

        /**
         * CAS：Compare and Swap，比较并交换。
         java.util.concurrent包中借助CAS实现了区别于synchronouse同步锁的一种乐观锁。
         CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。
         */
        //sequence获取当前的值。并自增1。获取并自增函数getAndIncrement()可以解决并发的问题，确保并发情况下轮询的正确性。
        int currentSequence = sequence.getAndIncrement();
        //当存在权重值并且权限值不相等的情况
        if (maxWeight > 0 && minWeight < maxWeight) {
            //如果存在权重值各不相等的话，则采取带权重值的轮询策略
            //当前调用次数 取模 最大权重值。(0<mod<weightSum)
            int mod = currentSequence % weightSum;
            for (int i = 0; i < maxWeight; i++) {
                for (Map.Entry<Invoker<T>, MyRoundRobinLoadBalance.IntegerWrapper> each : invokerToWeightMap.entrySet()) {
                    final Invoker<T> k = each.getKey();
                    final MyRoundRobinLoadBalance.IntegerWrapper v = each.getValue();
                    if (mod == 0 && v.getValue() > 0) {
                        //mod=0 表示这是按服务注册顺序，最近一次应该被调用的服务。
                        // 如果他的权重值大于0 ，则返回该服务。
                        // 如果小于0 则表示当前服务权重值相对较小，则顺序执行下一个服务，直到满足权重值大于0返回。
                        return k;
                    }
                    if (v.getValue() > 0) {
                        //这种情况下，是已经被轮询调用过的服务。取模值递减1，轮询下一个服务。
                        v.decrement();
                        //服务的权重值减1
                        mod--;
                    }
                }
            }
        }
        // Round robin 权重值是一样的 取模长度 顺序返回调用（不带权重策略）
        return invokers.get(currentSequence % length);
    }

    private static final class IntegerWrapper {
        private int value;

        public IntegerWrapper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void decrement() {
            this.value--;
        }
    }

}

