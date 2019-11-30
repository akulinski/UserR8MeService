package com.akulinski.userr8meservice.core.exceptions.repository;


import lombok.Data;

@Data
public class UserNotFoundException extends RuntimeException {
  private String username;

  public UserNotFoundException(String username) {
    super(String.format("User %s Not Found", username));
    this.username = username;
  }

  public UserNotFoundException(String message, String username) {
    super(message);
    this.username = username;
  }
}
