package com.akulinski.userr8meservice.core.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class UserDTO implements Serializable {
  private final String username;
  private final Double currentRating;
  private final String link;
  private final int followersCount;
  private final int commentsCount;
}
