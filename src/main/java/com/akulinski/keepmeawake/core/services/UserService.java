package com.akulinski.keepmeawake.core.services;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.domain.dto.UserDTO;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User mapDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        return user;
    }


    public Question choseQuestion(User user, Set<String> questionValues) {

        List<Question> byValueNotInAndCategoryIn = userRepository.findQuestions(user, questionValues);

        if (byValueNotInAndCategoryIn.isEmpty()) {
            throw new IllegalStateException("No new questions for user");
        }

        final Question question = byValueNotInAndCategoryIn.get(0);

        user.getAskedQuestions().add(question);

        userRepository.save(user);

        return question;
    }

}
