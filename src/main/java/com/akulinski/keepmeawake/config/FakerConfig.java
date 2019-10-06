package com.akulinski.keepmeawake.config;

import com.akulinski.keepmeawake.core.domain.Category;
import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.repository.QuestionRepository;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
public class FakerConfig {

    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;
    private final Faker faker = new Faker();

    public FakerConfig(UserRepository userRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void generateData() {

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote());
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);

            return user;
        }).limit(100 - userRepository.count()).forEach(userRepository::save);

        Stream.generate(() -> {
            Question question = new Question();
            question.setValue(faker.hobbit().quote());
            question.setDescription(faker.hobbit().thorinsCompany());
            question.setCategory(Category.GENERAL_KNOWLEDGE);
            return question;
        }).limit(100 - questionRepository.count()).forEach(questionRepository::save);

    }
}
