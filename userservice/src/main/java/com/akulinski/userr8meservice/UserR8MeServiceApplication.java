package com.akulinski.userr8meservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableMongoAuditing
public class UserR8MeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserR8MeServiceApplication.class, args);
    }

}
