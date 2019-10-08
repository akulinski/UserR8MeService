package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Question;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {

    private final MongoTemplate mongoTemplate;

    public QuestionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable(cacheNames = "questions")
    @Override
    public List<Question> findAll() {
        return mongoTemplate.findAll(Question.class);
    }

    @CachePut(cacheNames = "questions", key = "#question.id")
    @Override
    public Question save(Question question) {
        return mongoTemplate.save(question);
    }

    @CacheEvict(cacheNames = "questions", allEntries = true)
    @Override
    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Question.class);
    }

    @Cacheable(cacheNames = "questions", key = "#id")
    @Override
    public Optional<Question> findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        return Optional.ofNullable(mongoTemplate.findOne(query, Question.class));
    }

    @Override
    public int count() {
        return mongoTemplate.findAll(Question.class).size();
    }
}
