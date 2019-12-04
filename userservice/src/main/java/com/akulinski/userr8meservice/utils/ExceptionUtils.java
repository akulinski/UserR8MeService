package com.akulinski.userr8meservice.utils;

import com.akulinski.userr8meservice.core.exceptions.NoCommentException;
import com.akulinski.userr8meservice.core.exceptions.repository.UserNotFoundException;

import java.util.function.Supplier;

public class ExceptionUtils {
  public static final String NO_USER_FOUND_WITH_USERNAME_S = "No user found with username: %s";
  public static final String USER_S_HAS_NO_COMMENT_S_OR_POSTER_IS_NOT_OWNER_OF_THAT_COMMENT = "User: %s has no comment: %s or poster is not owner of that comment ";

  ExceptionUtils() {

  }

  static public Supplier<UserNotFoundException> getUserNotFoundExceptionSupplier(String message, String name) {
    return () -> new UserNotFoundException(message, name);
  }

  static public Supplier<NoCommentException> getNoCommentException(String username, String comment) {
    return () -> new NoCommentException(String.format(USER_S_HAS_NO_COMMENT_S_OR_POSTER_IS_NOT_OWNER_OF_THAT_COMMENT, username, comment), username, comment);
  }
}
