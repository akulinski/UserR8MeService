package com.akulinski.userr8meservice.core.exceptions.validation;

import lombok.Data;

@Data
public class FollowException extends IllegalArgumentException {

  private String follower;
  private String following;
  private String fault;


  public FollowException(String message, String follower, String following, String fault) {
    super(message);
    this.follower = follower;
    this.following = following;
    this.fault = fault;
  }

}
