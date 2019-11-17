package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class RatingResource {

    private final RatingService ratingService;

    @GetMapping("/{question}")
    public ResponseEntity getRateForUserQuestion(Principal principal, @PathVariable("question") String question){
        var rateForUser = ratingService.getRateForUser(principal, question);
        var rateDTO = new RateDTO(principal.getName(), rateForUser, question);
        return ResponseEntity.ok(rateDTO);
    }

}
