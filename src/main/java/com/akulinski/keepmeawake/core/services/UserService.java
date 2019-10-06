package com.akulinski.keepmeawake.core.services;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.domain.UserDTO;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    public UserService(MongoTemplate mongoTemplate, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
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
        Query query = new Query();
        query.addCriteria(Criteria.where("value").nin(questionValues).and("category").in(user.getCategories()));
        var byValueNotInAndCategoryIn = mongoTemplate.find(query, Question.class);

        if (byValueNotInAndCategoryIn.isEmpty()) {
            throw new IllegalStateException("No new questions for user");
        }
        final Question question = byValueNotInAndCategoryIn.get(0);
        user.getAskedQuestions().add(question);
        userRepository.save(user);

        return question;
    }
}
