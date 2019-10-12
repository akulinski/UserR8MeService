package com.akulinski.keepmeawake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableMongoAuditing
public class KeepmeawakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeepmeawakeApplication.class, args);
	}

}
