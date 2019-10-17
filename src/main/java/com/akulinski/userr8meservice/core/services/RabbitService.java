package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;


    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void calculateUserStats(User user) {
        String routingKey = "user.calculate";

        rabbitTemplate.convertAndSend("user", routingKey, user);
    }

}
