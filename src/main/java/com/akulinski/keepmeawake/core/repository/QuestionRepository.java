package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
}
