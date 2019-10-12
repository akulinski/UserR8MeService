package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    User save(User user) throws DuplicateValueException;

    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<User> findById(String id);

    List<Question> findQuestions(User user, Set<String> questionValues);

    void deleteById(String id);

    int count();

    boolean isLinkPresent(String link);

    Optional<User> findUserByLink(String link);

}
