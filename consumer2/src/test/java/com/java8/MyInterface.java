package com.java8;

@FunctionalInterface
public interface MyInterface {

	String  cehsi();

	default String getName(){
		return "呵呵呵";
	}
	
	public static void show(){
		System.out.println("接口中的静态方法");
	}

}
