package com.akulinski.pushservice.config

import com.akulinski.pushservice.core.domain.ChangeDirection
import com.akulinski.pushservice.core.domain.messages.UserRateChangedMessage
import com.github.javafaker.Faker
import lombok.extern.slf4j.Slf4j
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener

@Configuration
@Profile("dev")
@Slf4j
class FakerConfig(@Autowired private val rabbitTemplate: RabbitTemplate,
                  @Value("\${properties.exchange}")
                  private var exchange: String) {

  val faker = Faker()

  @EventListener(ApplicationReadyEvent::class)
  fun mockEvents() {
    for (i in 0..2) {
      val userRateChangedMessage = UserRateChangedMessage(faker.name().username(), setOf(faker.name().username(), faker.name().username()), faker.random().nextDouble(), faker.random().nextDouble())

      if(userRateChangedMessage.newValue > userRateChangedMessage.oldValue){
        rabbitTemplate.convertAndSend(exchange, ChangeDirection.UP.name, userRateChangedMessage)
      }else{
        rabbitTemplate.convertAndSend(exchange, ChangeDirection.DOWN.name, userRateChangedMessage)
      }
    }
  }
}
