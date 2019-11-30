package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.*;
import com.akulinski.userr8meservice.core.domain.dto.*;
import com.akulinski.userr8meservice.core.exceptions.validation.FollowException;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.utils.ExceptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

  private PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  public User mapDTO(CreateUserDTO createUserDTO) {
    User user = new User();
    user.setUsername(createUserDTO.getUsername());
    user.setEmail(createUserDTO.getEmail());
    user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
    user.setIsEnabled(Boolean.FALSE);
    return user;
  }

  public CreateUserDTO mapUser(User user) {
    return new CreateUserDTO(user.getUsername(), user.getPassword(), user.getEmail());
  }

  private String getLinkThatIsNotPresent(String id) {
    return RandomStringUtils.randomNumeric(30) + "||" + id;
  }

  public UserDTO getUserDTOByUsername(String username) {
    final var user = userRepository.findByUsername(username)
      .orElseThrow(() -> new IllegalStateException(String.format("No user with username: %s", username)));

    return new UserDTO(user.getUsername(), user.getCurrentRating(), user.getLink(), user.getFollowers().size(), user.getComments().size());
  }

  public User getUser(CreateUserDTO createUserDTO) {
    User user = mapDTO(createUserDTO);
    Set<Authority> authorities = new HashSet<>();
    authorities.add(new Authority(AuthorityType.USER));
    user.setAuthorities(authorities);

    user = userRepository.save(user);

    String link = getLinkThatIsNotPresent(user.getId());

    user.setLink(link);

    userRepository.save(user);

    return user;
  }

  public User changePassword(String username, ChangePasswordDTO changePasswordDTO) throws IllegalArgumentException {
    final var byUsername = userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username));

    if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), byUsername.getPassword())) {
      byUsername.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
      userRepository.save(byUsername);
    } else {
      throw new IllegalArgumentException("Old password does not match");
    }

    return byUsername;
  }


  public void addRateToUser(RateDTO rateDTO, UserDTO toRate, UserDTO rater) {
    Rate rate = new Rate();
    rate.setRate(rateDTO.getRating());
    rate.setSender(rater.getUsername());

    final var toRateUser = userRepository.findByUsername(toRate.getUsername()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, toRate.getUsername()));

    toRateUser.getRates().add(rate);

    Map<String, List<Rate>> grouped = toRateUser.getRates().stream().collect(Collectors.groupingBy(Rate::getQuestion));
    grouped.forEach((s, rates) -> {
      double average = rates.stream().mapToDouble(Rate::getRate).average().getAsDouble();
      toRateUser.getRatesMap().put(s, average);
    });

    userRepository.save(toRateUser);
  }

  public Comment createAndSaveComment(CommentDTO commentDTO, UserDTO receiverDTO, UserDTO posterDTO) {
    Comment comment = new Comment();
    comment.setComment(commentDTO.getComment());
    comment.setCommenterLogin(posterDTO.getUsername());

    final var receiver = userRepository.findByUsername(receiverDTO.getUsername()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, receiverDTO.getUsername()));
    final var poster = userRepository.findByUsername(posterDTO.getUsername()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, posterDTO.getUsername()));

    comment.setCommenterLink(poster.getLink());
    comment.setTimestamp(new Date().toInstant());

    receiver.getComments().add(comment);
    userRepository.save(receiver);

    return comment;
  }

  public void deleteByName(String username) {
    final var id = userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getId();

    userRepository.deleteById(id);
  }

  @Cacheable(cacheNames = "by-id", key = "#id")
  public User getById(String id) {
    return userRepository.findById(id).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, id));
  }

  public void deleteById(String id) {
    userRepository.deleteById(id);
  }

  @Cacheable(cacheNames = "all-users")
  public List<User> getAll() {
    return userRepository.findAll();
  }

  @Cacheable(cacheNames = "rating")
  public Double getSum(UserDTO userDTO) {

    final var byUsername = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, userDTO.getUsername()));

    final var average = byUsername.getRates().stream()
      .mapToDouble(Rate::getRate).average().orElse(-1.0);

    byUsername.setCurrentRating(average);

    userRepository.save(byUsername);
    return average;
  }

  public void followUser(String follower, String followed) {
    final var followerUser = userRepository.findByUsername(follower).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, follower));
    final var followedUser = userRepository.findByUsername(followed).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, followed));

    followedUser.getFollowers().add(followerUser.getUsername());
    userRepository.save(followedUser);

    followerUser.getFollowing().add(followedUser.getUsername());
    userRepository.save(followerUser);
  }

  public void unFollowUser(String follower, String followed) {

    final var followerUser = userRepository.findByUsername(follower).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, follower));

    final var followedUser = userRepository.findByUsername(follower).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, followed));

    if (followedUser.getFollowers().contains(followerUser.getUsername())) {
      followedUser.getFollowers().remove(followerUser.getUsername());
      userRepository.save(followedUser);
    } else {
      throw new FollowException(String.format("User %s is not following %s", follower, followed), follower, followed, followed);
    }

    if (followerUser.getFollowing().contains(followedUser.getUsername())) {
      followerUser.getFollowing().remove(followedUser.getUsername());
      userRepository.save(followerUser);
    } else {
      throw new FollowException(String.format("User %s is not followed by %s", followed, follower), followed, follower, follower);
    }
  }

  @Cacheable(cacheNames = "regex-response-paged")
  public List<RegexResponseElement> findByRegexPage(int page, String regex) {

    Pageable pageable = PageRequest.of(page, 5);

    return userRepository.pageRegex(regex, pageable).stream()
      .map(usr -> new RegexResponseElement(usr.getId(), usr.getUsername(), regex)).collect(Collectors.toList());
  }

  public List<RegexResponseElement> findByRegex(String regex) {
    return userRepository.findAllUsersByRegex(regex).stream()
      .map(user -> new RegexResponseElement(user.getId(), user.getUsername(), regex)).collect(Collectors.toList());
  }
}
