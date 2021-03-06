package com.akulinski.userr8meservice.core.exceptions;

import lombok.Data;

@Data
public class NoCommentException extends RuntimeException {
  private String username;
  private String comment;

  public NoCommentException(String message, String username, String comment) {
    super(message);
    this.username = username;
    this.comment = comment;
  }
}
