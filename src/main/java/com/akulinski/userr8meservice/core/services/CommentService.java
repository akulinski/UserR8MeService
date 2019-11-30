package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.utils.ExceptionUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class CommentService {

  private final UserRepository userRepository;

  @Cacheable(cacheNames = "comments", key = "#username")
  public Set<Comment> getCommentsForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getComments();
  }

  public void addCommentToUser(String username, Comment comment) {

    final var user = userRepository.findByUsername(username)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    user.getComments().add(comment);
    userRepository.save(user);
  }

  public void removeComment(String username, String poster, Comment comment) {

    final var user = userRepository.findByUsername(username)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    final var found = user.getComments().stream().filter(c -> c.equals(comment) && c.getCommenterLogin().equals(poster)).findFirst()
      .orElseThrow(ExceptionUtils.getNoCommentException(username, comment.getComment()));

    user.getComments().remove(found);
    userRepository.save(user);


  }

  public void removeComment(String username, Comment comment) {

    final var user = userRepository.findByUsername(username)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    final var found = user.getComments().stream().filter(c -> c.equals(comment)).findFirst()
      .orElseThrow(ExceptionUtils.getNoCommentException(username, comment.getComment()));

    user.getComments().remove(found);
    userRepository.save(user);
  }
}
