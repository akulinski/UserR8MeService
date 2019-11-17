package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/rate")
    public ResponseEntity rateUser(Principal principal, @RequestBody RateDTO rateDTO) {
        try {
            ratingService.rateUser(principal.getName(), rateDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
