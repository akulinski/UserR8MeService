package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Rate;
import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

  private final UserRepository userRepository;


  @Cacheable(cacheNames = "rates", key = "#principal.name||#question")
  public Double getRateForUser(Principal principal, String question) {
    var user = userRepository.findByUsername(principal.getName()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, principal.getName()));
    var avg = user.getRates().stream().filter(rate -> rate.getQuestion().equals(question)).mapToDouble(Rate::getRate).average().orElse(-1);

    user.getRatesMap().put(question, avg);
    userRepository.save(user);

    return avg;
  }

  public Map<String, Double> getAverageOfRatesForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getRatesMap();
  }

  public void rateUser(String rater, RateDTO rateDTO) {
    var receiver = userRepository.findByUsername(rateDTO.getReceiver()).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, rateDTO.getReceiver()));

    var rate = new Rate(rateDTO.getRating(), rater, rateDTO.getQuestion());
    receiver.getRates().add(rate);
    userRepository.save(receiver);

  }

  @Cacheable(cacheNames = "rates-for-user", key = "#username")
  public Set<Rate> getRatesForUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(ExceptionUtils.getUserNotFoundExceptionSupplier(ExceptionUtils.NO_USER_FOUND_WITH_USERNAME_S, username)).getRates();
  }
}
