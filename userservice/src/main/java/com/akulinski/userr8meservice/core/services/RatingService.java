package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.ChangeDirection;
import com.akulinski.userr8meservice.core.domain.Rate;
import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.domain.messages.UserRateChangedMessage;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

  private final UserRepository userRepository;
  private final RabbitTemplate rabbitTemplate;

  @Value("${properties.exchange}")
  private String exchange;

  @Cacheable(cacheNames = "rates", key = "#principal.name||#question")
  public Double getRateForUser(Principal principal, String question) {
    var user = userRepository.findByUsername(principal.getName()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, principal.getName()));
    var avg = user.getRates().stream().filter(rate -> rate.getQuestion().equals(question)).mapToDouble(Rate::getRate).average().orElse(-1);

    user.getRatesMap().put(question, avg);
    userRepository.save(user);

    return avg;
  }

  private void updateUserRate(User user, Double newRate, Double oldRate) {

    final var changeDirection = compareOldRating(newRate, oldRate);

    if (changeDirection != ChangeDirection.NONE) {
      final var userRateChangedMessage = new UserRateChangedMessage(user.getUsername(), user.getFollowers(), oldRate, user.getCurrentRating());
      rabbitTemplate.convertAndSend(exchange, changeDirection.name(), userRateChangedMessage);
    }

    user.setCurrentRating(newRate);
    userRepository.save(user);
  }

  public Map<String, Double> getAverageOfRatesForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getRatesMap();
  }

  public void rateUser(String rater, RateDTO rateDTO) {
    var receiver = userRepository.findByUsername(rateDTO.getReceiver()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, rateDTO.getReceiver()));
    final var oldAverage = receiver.getCurrentRating();

    var rate = new Rate(rateDTO.getRating(), rater, rateDTO.getQuestion(), Instant.now());
    receiver.getRates().add(rate);
    receiver.getRatesMap().merge(rateDTO.getQuestion(), rateDTO.getRating(), (a, b) -> (a + b) / 2);
    final var averageOfRatesForUser = receiver.getRatesMap();
    final var average = averageOfRatesForUser.values().stream().mapToDouble(v -> v).average().orElse(-1.0);
    receiver.setCurrentRating(average);
    receiver.setRatesMap(averageOfRatesForUser);
    userRepository.save(receiver);

    updateUserRate(receiver, average, oldAverage);
  }

  private ChangeDirection compareOldRating(Double newRating, Double oldRate) {

    if (newRating > oldRate) {
      return ChangeDirection.UP;
    } else if (newRating < oldRate) {
      return ChangeDirection.DOWN;
    }

    return ChangeDirection.NONE;
  }

  @Cacheable(cacheNames = "rates-for-user", key = "#username")
  public Set<Rate> getRatesForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getRates();
  }
}
