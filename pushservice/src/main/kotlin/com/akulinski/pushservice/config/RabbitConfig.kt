package com.akulinski.pushservice.config

import com.akulinski.pushservice.core.domain.ChangeDirection
import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig(@Value("\${properties.pushQueueUp}")
                   private var pushNotificationQueueUp: String,
                   @Value("\${properties.pushQueueDown}")
                   private var pushNotificationQueueDown: String,
                   @Value("\${properties.exchange}")
                   private var exchange: String) {

  @Bean
  fun appExchange(): TopicExchange {
    return TopicExchange(exchange)
  }

  @Bean
  fun pushQueueUp(): Queue{
    return Queue(pushNotificationQueueUp, true)
  }

  @Bean
  fun pushQueueDown(): Queue{
    return Queue(pushNotificationQueueDown, true)
  }

  @Bean
  fun bindPushMessagesUpTendency(): Binding{
    return BindingBuilder.bind(pushQueueUp()).to(appExchange()).with(ChangeDirection.UP)
  }

  @Bean
  fun bindPushMessagesDownTendency(): Binding{
    return BindingBuilder.bind(pushQueueDown()).to(appExchange()).with(ChangeDirection.DOWN)
  }
}
