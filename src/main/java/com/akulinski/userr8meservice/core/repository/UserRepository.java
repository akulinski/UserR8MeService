package com.akulinski.userr8meservice.core.repository;

import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.RegexResponseElement;
import com.akulinski.userr8meservice.core.exceptions.repository.DuplicateValueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user) throws DuplicateValueException;

    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<User> findById(String id);

    void deleteById(String id);

    int count();

    boolean isLinkPresent(String link);

    Optional<User> findUserByLink(String link);

    void deleteAll();

    List<User> findAllUsersByRegex(String regex);

    Page<User> pageRegex(String regex, Pageable pageable);
}
