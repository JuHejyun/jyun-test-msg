package com.jyun.test.msg.web;

import com.jyun.test.msg.web.zkserer.zkClientss;
import com.jyun.test.msg.web.zkserer.zkServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jyun.test"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public zkServers zkServers(){
		return new zkServers();
	}

	@Bean
	public zkClientss zkClientss(){
		return new zkClientss();
	}

}
