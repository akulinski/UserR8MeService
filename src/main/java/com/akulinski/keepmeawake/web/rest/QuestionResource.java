package com.akulinski.keepmeawake.web.rest;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.dto.QuestionDTO;
import com.akulinski.keepmeawake.core.repository.QuestionRepository;
import com.akulinski.keepmeawake.core.services.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
@Slf4j
public class QuestionResource {

    private final QuestionRepository questionRepository;

    private final QuestionService questionService;

    public QuestionResource(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Cacheable(cacheNames = "questions")
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @CachePut(cacheNames = "questions")
    public ResponseEntity createQuestion(@RequestBody QuestionDTO questionDTO) {
        final Question question = questionService.mapDTO(questionDTO);
        final Question save = questionRepository.save(question);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/id/{id}")
    @Cacheable(cacheNames = "questions")
    public ResponseEntity getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(questionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("No Question with id %s", id))));
    }


    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
