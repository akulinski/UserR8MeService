package com.akulinski.keepmeawake.web.rest;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.QuestionDTO;
import com.akulinski.keepmeawake.core.repository.QuestionRepository;
import com.akulinski.keepmeawake.core.services.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/question")
@Slf4j
public class QuestionResource {

    private final QuestionRepository questionRepository;

    private final QuestionService questionService;

    public QuestionResource(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity createQuestion(@RequestBody QuestionDTO questionDTO) {
        final Question question = questionService.mapDTO(questionDTO);
        final Question save = questionRepository.save(question);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(questionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("No Question with id %s", id))));
    }


    @DeleteMapping("/id/{id}")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
