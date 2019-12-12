package com.akulinski.userr8meservice.core.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserRateChangedMessage {
  private static final String TOPIC = "STATUS_CHANGE";

  private final String  username;
  private final Set<String> followers;
  private final Double oldValue;
  private final Double newValue;
}
