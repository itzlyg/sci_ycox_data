package com.sci.ycox.springflink;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//@SpringBootApplication
//@EnableConfigurationProperties
//@MapperScan(basePackages = {"com.sci.ycox.**.dao"})
public class SpringflinkAppliation  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringflinkAppliation.class, args);
	}

	@Override//为了打包springboot项目
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(this.getClass());
	}
}
