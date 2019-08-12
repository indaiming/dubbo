package com.userserviceprovider;

import com.alibaba.dubbo.common.utils.AtomicPositiveInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceProviderApplicationTests {

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

    //记录 权重服务的调用总次数
    private final AtomicPositiveInteger weightServiceCount13 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount8 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount5 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount3 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount2 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount1 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount12 = new AtomicPositiveInteger(0);
    private final AtomicPositiveInteger weightServiceCount24 = new AtomicPositiveInteger(0);
    //sequences
    private final ConcurrentMap<String, AtomicPositiveInteger> sequences = new ConcurrentHashMap<String, AtomicPositiveInteger>();


    @Test
    public void contextLoads1() {
        //模拟多线程 并发20000调用 服务
        for (int i = 0; i < 40000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getId());
                    contextLoads();
                }
            }).start();
        }
    }

    @Test
    public void contextLoads() {
        final LinkedHashMap<String, IntegerWrapper> invokerToWeightMap = new LinkedHashMap<String, UserServiceProviderApplicationTests.IntegerWrapper>();
        //模拟提供八个服务，权重值不同，
        invokerToWeightMap.put("服务1", new UserServiceProviderApplicationTests.IntegerWrapper(13));//大权重服务
        invokerToWeightMap.put("服务2", new UserServiceProviderApplicationTests.IntegerWrapper(8));//大权重服务
        invokerToWeightMap.put("服务3", new UserServiceProviderApplicationTests.IntegerWrapper(5));//大权重服务
        invokerToWeightMap.put("服务4", new UserServiceProviderApplicationTests.IntegerWrapper(3));//小权重服务
        invokerToWeightMap.put("服务5", new UserServiceProviderApplicationTests.IntegerWrapper(2));//小权重服务
        invokerToWeightMap.put("服务6", new UserServiceProviderApplicationTests.IntegerWrapper(1));//小权重服务
        invokerToWeightMap.put("服务7", new UserServiceProviderApplicationTests.IntegerWrapper(12));//大权重服务
        invokerToWeightMap.put("服务8", new UserServiceProviderApplicationTests.IntegerWrapper(24));//大权重服务

        boolean flag = true;

        //虚拟k
        String key = "serviceId+method";

        AtomicPositiveInteger sequence = sequences.get(key);
        if (sequence == null) {
            //如果sequences不存在key 则添加key:服务名+方法名 vlaue：正整数（初始设置为为1930653908）
            sequences.putIfAbsent(key, new AtomicPositiveInteger(2000000000));
            //获取value值
            sequence = sequences.get(key);
        }
        //获取并自增 线程安全的
        int currentSequence = sequence.getAndIncrement();


        //测试输出 各个权重值服务的 调用次数
        if (currentSequence == 2000039999) {
            //第2000020000次 输出 大小权重各自的调用次数。
            System.out.println("权重为13的服务调用总次数：" + weightServiceCount13.getAndIncrement());
            System.out.println("权重为24的服务调用总次数：" + weightServiceCount24.getAndIncrement());
            System.out.println("权重为12的服务调用总次数：" + weightServiceCount12.getAndIncrement());
            System.out.println("权重为8的服务调用总次数：" + weightServiceCount8.getAndIncrement());
            System.out.println("权重为5的服务调用总次数：" + weightServiceCount5.getAndIncrement());
            System.out.println("权重为3的服务调用总次数：" + weightServiceCount3.getAndIncrement());
            System.out.println("权重为2的服务调用总次数：" + weightServiceCount2.getAndIncrement());
            System.out.println("权重为1的服务调用总次数：" + weightServiceCount1.getAndIncrement());
        }

        int weightSum = 70;//总权重数

        //当前调用次数 取模 最大权重值。(0<mod<weightSum)
        int mod = currentSequence % weightSum;
        for (int i = 0; i < 12; i++) {
            for (Map.Entry<String, UserServiceProviderApplicationTests.IntegerWrapper> each : invokerToWeightMap.entrySet()) {
                final String k = each.getKey();
                final UserServiceProviderApplicationTests.IntegerWrapper v = each.getValue();
                if (mod == 0 && v.getValue() > 0 && flag) {
                    //各个服务被调用后 各自增1
                    switch (k) {
                        case "服务1":
                            weightServiceCount13.incrementAndGet();
                            break;
                        case "服务2":
                            weightServiceCount8.incrementAndGet();
                            break;
                        case "服务3":
                            weightServiceCount5.incrementAndGet();
                            break;
                        case "服务4":
                            weightServiceCount3.incrementAndGet();
                            break;
                        case "服务5":
                            weightServiceCount2.incrementAndGet();
                            break;
                        case "服务6":
                            weightServiceCount1.incrementAndGet();
                            break;
                        case "服务7":
                            weightServiceCount12.incrementAndGet();
                            break;
                        case "服务8":
                            weightServiceCount24.incrementAndGet();
                            break;
                    }
                    System.out.println("mod==>" + mod + ",调用==>" + k + ",weight==>" + v.getValue());
                    flag = false;
                }
//                else {
//                    System.out.println("失败的！！！！！！！！！！currentSequence==>" + currentSequence + ",调用【】==>" + k + ",weight==>" + v.getValue());
//                }
                if (v.getValue() > 0) {
                    v.decrement();
                    mod--;
                }
            }
        }
    }
}

