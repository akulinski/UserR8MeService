package com.akulinski.userr8meservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Profile("mongo-cluster")
@Configuration
public class MongoConfig {

    @Bean
    public MongoTransactionManager txManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
