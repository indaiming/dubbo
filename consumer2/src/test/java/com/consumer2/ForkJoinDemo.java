package com.consumer2;/**
 * @Author daim
 * @Description //TODO end
 * @Date date
 **/

import java.util.concurrent.RecursiveTask;

/**
 * @Classname ForkJoin
 * @Description TODO
 * @Date 2019/8/12 
 * @Created by daim
 */
public class ForkJoinDemo extends RecursiveTask<Long> {
    private static final long serialVersionUID = 13475679780L;

    private long start;
    private long end;

    private static final long THRESHOLD = 100000L; //临界值

    public ForkJoinDemo(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long length = end -start;
        if (length<=THRESHOLD){
            long sum = 0L;
            for (long i = start; i <=end ; i++) {
                sum += i;
            }
            return sum;
        }else{
            long middle = (start+end)/2;
            ForkJoinDemo left = new ForkJoinDemo(start, middle);
            left.fork();
            ForkJoinDemo right = new ForkJoinDemo(middle+1, end);
            right.fork();
            return left.join()+right.join();
        }
    }
}
