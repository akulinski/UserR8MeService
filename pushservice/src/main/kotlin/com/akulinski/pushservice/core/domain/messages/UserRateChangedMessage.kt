package com.akulinski.pushservice.core.domain.messages

import java.io.Serializable

class UserRateChangedMessage(
   val username: String,
   val followers: Set<String>,
   val oldValue: Double,
   val newValue: Double
) : Serializable {
  val topic = "STATUS_CHANGE"
}
