package com.akulinski.pushservice.core.services

import com.akulinski.pushservice.core.domain.ChangeDirection
import com.akulinski.pushservice.core.domain.FollowerEntity
import com.akulinski.pushservice.core.domain.StatusChangeEntity
import com.akulinski.pushservice.core.domain.messages.UserRateChangedMessage
import com.akulinski.pushservice.core.repositories.FollowerRepository
import com.akulinski.pushservice.core.repositories.StatusChangeRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class PushEventService(
  val statusChangeRepository: StatusChangeRepository,
  val followerRepository: FollowerRepository
) {

  @RabbitListener(queues = ["\${properties.pushQueueUp}"])
  fun handleUserRateChangeUp(userRateChangedMessage: UserRateChangedMessage){
    createStatusChangeEntity(userRateChangedMessage, ChangeDirection.UP)
  }

  private fun createStatusChangeEntity(userRateChangedMessage: UserRateChangedMessage, changeDirection: ChangeDirection): StatusChangeEntity {
    val statusChangeEntity = StatusChangeEntity()
    statusChangeEntity.changeDirection = changeDirection
    statusChangeEntity.newValue = userRateChangedMessage.newValue
    statusChangeEntity.oldValue = userRateChangedMessage.oldValue
    statusChangeEntity.username = userRateChangedMessage.username
    statusChangeEntity.followers = setOf()
    statusChangeRepository.save(statusChangeEntity)

    val followers = userRateChangedMessage.followers.map { username -> FollowerEntity(username = username, statusChangeEntity = statusChangeEntity) }
    followers.forEach{ follower -> followerRepository.save(follower)}

    return statusChangeEntity
  }

  @RabbitListener(queues = ["\${properties.pushQueueDown}"])
  fun handleUserRateChangeDown(userRateChangedMessage: UserRateChangedMessage){
     createStatusChangeEntity(userRateChangedMessage, ChangeDirection.DOWN)
  }
}
