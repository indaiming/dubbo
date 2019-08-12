package com.java8;

import java.util.concurrent.RecursiveTask;

public class ForkJoinCalculate extends RecursiveTask<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 13475679780L;
	
	private long start;
	private long end;
	
	private static final long THRESHOLD = 10000L; //临界值
	
	public ForkJoinCalculate(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	protected Long compute() {
		long length = end - start;
		
		if(length <= THRESHOLD){
			//小于边界值，直接执行。
			long sum = 0;
			
			for (long i = start; i <= end; i++) {
				sum += i;
			}
			
			return sum;
		}else{
			//大于边界值的，拆分成两个子任务
			long middle = (start + end) / 2;
			
			ForkJoinCalculate left = new ForkJoinCalculate(start, middle);
			left.fork(); //拆分，并将该子任务压入线程队列
			
			ForkJoinCalculate right = new ForkJoinCalculate(middle+1, end);
			right.fork();
			//两个子任务汇合返回。
			return left.join() + right.join();
		}
		
	}

}
