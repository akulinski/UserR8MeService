package com.akulinski.userr8meservice.config;

import com.akulinski.userr8meservice.core.domain.*;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
@Profile("dev")
@Slf4j
public class FakerConfig {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final Faker faker = new Faker();

    public FakerConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void run(){

        userRepository.findByUsername("admin").ifPresentOrElse((user) -> {
        }, () -> {
            User user = new User();
            user.setPassword(passwordEncoder.encode("admin"));
            user.setUsername("admin");
            user.setEmail("admin");

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.ADMIN));
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            Stream.generate(() -> {
                Rate rate = new Rate();
                rate.setRate(faker.random().nextDouble());
                return rate;
            }).limit(100).forEach(user.getRates()::add);

            Stream.generate(() -> {
                Comment comment = new Comment();
                comment.setComment(faker.book().title());
                return comment;
            }).limit(100).forEach(user.getComments()::add);

            userRepository.save(user);
        });


        User admin = userRepository.findByUsername("admin").get();

        userRepository.findByUsername("user").ifPresentOrElse((user) -> {
        }, () -> {
            User user = new User();
            user.setPassword(passwordEncoder.encode("user"));
            user.setUsername("user");
            user.setEmail("user");

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);


            Stream.generate(() -> {
                Rate rate = new Rate();
                rate.setSender(admin.getUsername());
                rate.setRate(faker.random().nextDouble());
                return rate;
            }).limit(100).forEach(user.getRates()::add);


            Stream.generate(() -> {
                Comment comment = new Comment();
                comment.setComment(faker.book().title());
                return comment;
            }).limit(100).forEach(user.getComments()::add);


            userRepository.save(user);
        });


        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote());

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            Stream.generate(() -> {
                Rate rate = new Rate();
                rate.setSender(admin.getUsername());
                rate.setRate(faker.random().nextDouble());
                return rate;
            }).limit(100).forEach(user.getRates()::add);

            Stream.generate(() -> {
                Comment comment = new Comment();
                comment.setComment(faker.book().title());
                return comment;
            }).limit(100).forEach(user.getComments()::add);

            return user;
        }).limit(100).forEach(userRepository::save);

    }
}