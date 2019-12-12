package com.akulinski.userr8meservice.config;

import com.akulinski.userr8meservice.core.domain.ChangeDirection;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Value("${properties.exchange}")
  private String exchange;

  @Value("${properties.pushQueueUp}")
  private String pushNotificationQueueUp;

  @Value("${properties.pushQueueDown}")
  private String pushNotificationQueueDown;

  @Bean
  public TopicExchange topicExchange(){
    return new TopicExchange(exchange);
  }

  @Bean
  public Queue pushQueueUp(){
    return new Queue(pushNotificationQueueUp);
  }

  @Bean
  public Queue pushQueueDown(){
    return new Queue(pushNotificationQueueDown);
  }

  @Bean
  public Binding bindPushMessagesUpTendency(){
    return BindingBuilder.bind(pushQueueUp()).to(topicExchange()).with(ChangeDirection.UP);
  }

  @Bean
  public Binding bindPushMessagesDownTendency(){
    return BindingBuilder.bind(pushQueueDown()).to(topicExchange()).with(ChangeDirection.DOWN);
  }
}
