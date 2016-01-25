package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.XForwardedFilter;

@SpringBootApplication
public class BootXForwardedApplication {

	@Bean
	public static XForwardedFilter xForwardedFilter() {
		return new XForwardedFilter();
	}

	public static void main(String[] args) {
		SpringApplication.run(BootXForwardedApplication.class, args);
	}
}
