package com.akulinski.userr8meservice.config;

import com.akulinski.userr8meservice.core.domain.*;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.core.services.PhotoService;
import com.amazonaws.util.IOUtils;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Configuration
@Profile("dev")
@Slf4j
public class FakerConfig {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final Faker faker = new Faker();

  private final PhotoService photoService;


  @EventListener(ApplicationReadyEvent.class)
  public void run() {

    userRepository.findByUsername("admin").ifPresentOrElse((user) -> {
    }, () -> {
      User user = new User();
      user.setPassword(passwordEncoder.encode("admin"));
      user.setUsername("admin");
      user.setEmail("admin@admin.com");
      File file = new File(getClass().getClassLoader().getResource("avatar.jpg").getFile());

      try {
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
          file.getName(), "text/plain", IOUtils.toByteArray(input));
        photoService.uploadAvatar(multipartFile, "admin");
      } catch (IOException e) {
        log.error(e.getLocalizedMessage());
      }

      Set<Authority> authorities = new HashSet<>();
      authorities.add(new Authority(AuthorityType.ADMIN));
      authorities.add(new Authority(AuthorityType.USER));
      user.setAuthorities(authorities);

      Stream.generate(() -> {
        Rate rate = new Rate();
        rate.setRate(faker.random().nextDouble());
        rate.setQuestion(faker.beer().style());
        return rate;
      }).limit(100).forEach(user.getRates()::add);

      Stream.generate(() -> {
        Comment comment = new Comment();
        comment.setComment(faker.book().title());
        comment.setCommenterLink(faker.avatar().image());
        comment.setTimestamp(new Date().toInstant());
        return comment;
      }).limit(100).forEach(user.getComments()::add);

      userRepository.save(user);
    });


    User admin = userRepository.findByUsername("admin").get();

    userRepository.findByUsername("user1").ifPresentOrElse((user) -> {
    }, () -> {
      User user = new User();
      user.setPassword(passwordEncoder.encode("user"));
      user.setUsername("user1");
      user.setEmail("user@user.com");

      File file = new File(getClass().getClassLoader().getResource("avatar.jpg").getFile());

      try {
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
          file.getName(), "text/plain", IOUtils.toByteArray(input));
        photoService.uploadAvatar(multipartFile, "user1");
      } catch (IOException e) {
        log.error(e.getLocalizedMessage());
      }

      Set<Authority> authorities = new HashSet<>();
      authorities.add(new Authority(AuthorityType.USER));
      user.setAuthorities(authorities);


      Stream.generate(() -> {
        Rate rate = new Rate();
        rate.setSender(admin.getUsername());
        rate.setRate(faker.random().nextDouble());
        rate.setQuestion(faker.beer().style());
        return rate;
      }).limit(100).forEach(user.getRates()::add);


      Stream.generate(() -> {
        Comment comment = new Comment();
        comment.setComment(faker.book().title());
        comment.setCommenterLink(faker.avatar().image());
        comment.setTimestamp(new Date().toInstant());
        return comment;
      }).limit(100).forEach(user.getComments()::add);


      userRepository.save(user);
    });


    if (userRepository.count() < 100) {

      Stream.generate(() -> {
        User user = new User();
        user.setUsername(faker.name().username() + RandomStringUtils.random(3));
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail("email." + RandomStringUtils.randomAlphanumeric(20) + "@email.com");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        Stream.generate(() -> {
          Rate rate = new Rate();
          rate.setSender(admin.getUsername());
          rate.setRate(faker.random().nextDouble());
          rate.setQuestion(faker.beer().style());
          return rate;
        }).limit(100).forEach(user.getRates()::add);

        Stream.generate(() -> {
          Comment comment = new Comment();
          comment.setComment(faker.book().title());
          comment.setCommenterLink(faker.avatar().image());
          comment.setTimestamp(new Date().toInstant());
          return comment;
        }).limit(100).forEach(user.getComments()::add);

        return user;
      }).limit(100).forEach(userRepository::save);
    }
  }
}
