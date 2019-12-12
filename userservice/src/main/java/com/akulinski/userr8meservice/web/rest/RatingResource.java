package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.dto.ProfileAverageDTO;
import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
@Slf4j
public class RatingResource {

  private final RatingService ratingService;

  @GetMapping("/{question}")
  public ResponseEntity getRateForUserQuestion(Principal principal, @PathVariable("question") String question) {
    var rateForUser = ratingService.getRateForUser(principal, question);
    var rateDTO = new RateDTO(principal.getName(), rateForUser, question);
    return ResponseEntity.ok(rateDTO);
  }

  @GetMapping("/profile-avg")
  public ResponseEntity getAverageForProfile(Principal principal) {
    final var averageOfRatesForUser = ratingService.getAverageOfRatesForUser(principal.getName());
    final var average = averageOfRatesForUser.values().stream().mapToDouble(v -> v).average().orElseThrow(() -> new IllegalStateException("No Rates yet for user"));

    return ResponseEntity.ok(new ProfileAverageDTO(principal.getName(), average));
  }

  @GetMapping("/avg")
  public ResponseEntity getAverageOfAllRates(Principal principal) {
    return ResponseEntity.ok(ratingService.getAverageOfRatesForUser(principal.getName()));
  }

  @PostMapping("/rate")
  public ResponseEntity rateUser(Principal principal, @RequestBody RateDTO rateDTO) {
    try {
      ratingService.rateUser(principal.getName(), rateDTO);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @GetMapping("/get-rates")
  public ResponseEntity getRates(Principal principal) {
    return ResponseEntity.ok(ratingService.getRatesForUser(principal.getName()));
  }


  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/get-rates/{username}")
  public ResponseEntity getRatesForUser(@PathVariable("username") String username) {
    return ResponseEntity.ok(ratingService.getRatesForUser(username));
  }

}
