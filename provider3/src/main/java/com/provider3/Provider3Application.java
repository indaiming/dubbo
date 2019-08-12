package com.provider3;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class Provider3Application {

	public static void main(String[] args) {
		SpringApplication.run(Provider3Application.class, args);
	}

}
