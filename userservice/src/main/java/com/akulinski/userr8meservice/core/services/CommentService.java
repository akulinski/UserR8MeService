package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.domain.dto.CommentDTO;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.utils.ExceptionUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
@AllArgsConstructor
public class CommentService {

  private final UserRepository userRepository;

  @Cacheable(cacheNames = "comments", key = "#username")
  public Set<Comment> getCommentsForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getComments();
  }

  public void addCommentToUser(CommentDTO commentDTO, String poster) {

    final var user = userRepository.findByUsername(poster)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, poster));

    var comment = new Comment(commentDTO.getComment(), poster, user.getLink(), new Date().toInstant());

    user.getComments().add(comment);
    userRepository.save(user);
  }

  public void removeComment(String username, String poster, String comment) {

    final var user = userRepository.findByUsername(username)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    final var found = user.getComments().stream().filter(c -> c.getComment().equals(comment) && c.getCommenterLogin().equals(poster)).findFirst()
      .orElseThrow(ExceptionUtils.getNoCommentException(username, comment));

    user.getComments().remove(found);
    userRepository.save(user);


  }

  public void removeComment(String username, String commentContent) {

    final var user = userRepository.findByUsername(username)
      .orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    final var found = user.getComments().stream().filter(c -> c.getComment().equals(commentContent)).findFirst()
      .orElseThrow(ExceptionUtils.getNoCommentException(username, commentContent));

    user.getComments().remove(found);
    userRepository.save(user);
  }
}
