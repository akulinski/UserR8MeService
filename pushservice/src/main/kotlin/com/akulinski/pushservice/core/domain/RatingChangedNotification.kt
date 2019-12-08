package com.akulinski.pushservice.core.domain

data class RatingChangedNotification(
  val username: String,
  val oldRating: Double,
  val newRating: Double,
  val changeDirection: ChangeDirection
) {

}
