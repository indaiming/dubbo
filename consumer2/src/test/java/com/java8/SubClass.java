package com.java8;

public class SubClass /*extends MyClass*/ implements MyFun, MyInterface{

	@Override
	public String cehsi() {
		return null;
	}


	@Override
	public String getName() {
		return MyInterface.super.getName();
	}
}
