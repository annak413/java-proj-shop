package com.example.demo_online_shop;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories("com.example.demo_online_shop.repository")
@EntityScan(basePackages = "com.example.demo_online_shop.model")
public class DemoOnlineShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoOnlineShopApplication.class, args);
	}

}
