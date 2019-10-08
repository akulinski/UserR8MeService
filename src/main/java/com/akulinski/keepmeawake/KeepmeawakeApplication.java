package com.akulinski.keepmeawake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KeepmeawakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeepmeawakeApplication.class, args);
	}

}
