package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Category;
import com.akulinski.keepmeawake.core.domain.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findByValueNotInAndCategoryIn(Set<String> values, Set<Category> categories);
}
