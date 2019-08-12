package com.java8;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestSimpleDateFormat {
	
	public static void main(String[] args) throws Exception {
		
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Callable<Date> task = new Callable<Date>() {

			@Override
			public Date call() throws Exception {
				return sdf.parse("20161121");
			}

		};

		ExecutorService pool = Executors.newFixedThreadPool(10);

		List<Future<Date>> results = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			results.add(pool.submit(task));
		}

		for (Future<Date> future : results) {
			System.out.println(future.get());
		}

		pool.shutdown();*/
		
		//解决多线程安全问题
		/*Callable<Date> task = new Callable<Date>() {

			@Override
			public Date call() throws Exception {
				return DateFormatThreadLocal.convert("20161121");
			}
			
		};

		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		List<Future<Date>> results = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			results.add(pool.submit(task));
		}
		
		for (Future<Date> future : results) {
			System.out.println(future.get());
		}
		
		pool.shutdown();*/
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
		DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");

		Callable<String> task = new Callable<String>() {

			@Override
			public String call() throws Exception {
				LocalDateTime ld = LocalDateTime.parse("2016 11 21 11 23 56", dtf);
				String format = ld.format(dtf1);
				ZoneId zone = ZoneId.of("Asia/Shanghai");
				Instant instant = ld.atZone(zone).toInstant();
				Date date = Date.from(instant);
				System.out.println(date);
				return format;
			}

		};

		ExecutorService pool = Executors.newFixedThreadPool(10);

		List<Future<String>> results = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			results.add(pool.submit(task));
		}

		for (Future<String> future : results) {
			System.out.println(future.get());
		}

		pool.shutdown();
	}

}
