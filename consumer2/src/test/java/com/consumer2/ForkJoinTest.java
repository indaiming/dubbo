package com.consumer2;/**
 * @Author daim
 * @Description //TODO end
 * @Date date
 **/

        import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * @Classname ForkJoinTest
 * @Description TODO
 * @Date 2019/8/12
 * @Created by daim
 */
public class ForkJoinTest {

    //forkJoin工作窃取模式
    @Test
    public  void test1 (){
        long start = System.currentTimeMillis();
        ForkJoinPool pools = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinDemo(0L, 100000000000L);
        Long sum = pools.invoke(task);
        long end = System.currentTimeMillis();
        System.out.println("耗费的时间为: " + (end - start)); //38-2459-19018

    }

    //java8并行流处理
    @Test
    public  void test2 (){

        long start = System.currentTimeMillis();
        long sum = LongStream.range(0L, 100000000001L).parallel().sum();
        long end = System.currentTimeMillis();
        System.out.println("耗费的时间为: " + (end - start)); //101-1650-11737
    }

}
