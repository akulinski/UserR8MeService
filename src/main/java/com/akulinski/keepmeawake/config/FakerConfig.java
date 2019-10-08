package com.akulinski.keepmeawake.config;

import com.akulinski.keepmeawake.core.domain.*;
import com.akulinski.keepmeawake.core.repository.QuestionRepository;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
@Profile("dev")
public class FakerConfig {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;

    private final Faker faker = new Faker();

    public FakerConfig(UserRepository userRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void run() {

        userRepository.findByUsername("admin").ifPresentOrElse((user) -> {
        }, () -> {
            User user = new User();
            user.setPassword(passwordEncoder.encode("admin"));
            user.setUsername("admin");
            user.setEmail("admin");
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);
            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.ADMIN));
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            userRepository.save(user);
        });

        userRepository.findByUsername("user").ifPresentOrElse((user) -> {
        }, () -> {
            User user = new User();
            user.setPassword(passwordEncoder.encode("user"));
            user.setUsername("user");
            user.setEmail("user");
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);
            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            userRepository.save(user);
        });

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote());
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            return user;
        }).limit(101 - userRepository.count()).forEach(userRepository::save);

        Stream.generate(() -> {
            Question question = new Question();
            question.setValue(faker.hobbit().quote());
            question.setDescription(faker.hobbit().thorinsCompany());
            question.setCategory(Category.GENERAL_KNOWLEDGE);
            return question;
        }).limit(100 - questionRepository.count()).forEach(questionRepository::save);
    }
}
