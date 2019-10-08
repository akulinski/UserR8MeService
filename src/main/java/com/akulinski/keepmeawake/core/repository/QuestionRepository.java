package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    List<Question> findAll();

    Question save(Question question);

    void deleteById(String id);

    Optional<Question> findById(String id);

    int count();
}
