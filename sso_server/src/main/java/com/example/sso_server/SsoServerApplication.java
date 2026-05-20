package com.example.sso_server;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.example.sso_server.mapper")
public class SsoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoServerApplication.class, args);
	}


	@Bean
	public MapperFactory mapperFactory() {
		return new MapperFactory();
	}

}
