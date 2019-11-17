package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Rate;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final UserRepository userRepository;


    @Cacheable(cacheNames = "rates", key = "#principal.name||#question")
    public Double getRateForUser(Principal principal, String question){
        var user = userRepository.findByUsername(principal.getName()).orElseThrow(()->new IllegalArgumentException(String.format("User with username: %s not found", principal.getName())));
        var avg = user.getRates().stream().filter(rate -> rate.getQuestion().equals(question)).mapToDouble(Rate::getRate).average().orElse(-1);

        user.getRatesMap().put(question, avg);
        userRepository.save(user);

        return avg;
    }
}
