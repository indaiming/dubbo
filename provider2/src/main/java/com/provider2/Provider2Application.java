package com.provider2;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class Provider2Application {

	public static void main(String[] args) {
		SpringApplication.run(Provider2Application.class, args);
	}

}
