package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.repository.UserRepository;
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
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username))).getComments();
    }

    public void addCommentToUser(String username, Comment comment) throws IllegalArgumentException{

        final var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username)));

        user.getComments().add(comment);
        userRepository.save(user);
    }

    public void removeComment(String username, String poster, Comment comment) throws IllegalArgumentException {

        final var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username)));

        final var found = user.getComments().stream().filter(c -> c.equals(comment) && c.getCommenterLogin().equals(poster)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("User: %s has no comment %s or poster is not owner of that comment ", username, comment)));

        user.getComments().remove(found);
        userRepository.save(user);


    }

    public void removeComment(String username, Comment comment) throws IllegalArgumentException {

        final var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username)));

        final var found = user.getComments().stream().filter(c -> c.equals(comment)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("User: %s has no comment %s or poster is not owner of that comment ", username, comment)));

        user.getComments().remove(found);
        userRepository.save(user);
    }
}
