package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Custom implementation of repository
 * due to cacheing problem
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    private final RedisTemplate<String, User> redisTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate, RedisTemplate redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    @CachePut(cacheNames = "users", key = "#user.id")
    @Override
    public User save(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final var userRedis = redisTemplate.opsForHash().get("users", username);

        if (userRedis == null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(username));

            final var one = mongoTemplate.findOne(query, User.class);

            redisTemplate.opsForHash().put("users", username, one);

            return Optional.ofNullable(one);
        }

        return Optional.of((User) userRedis);
    }

    @Cacheable(cacheNames = "users")
    @Override
    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    @Cacheable(cacheNames = "users", key = "#id")
    @Override
    public Optional<User> findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    @Cacheable(cacheNames = "questions-query", key = "#user.id")
    public List<Question> findQuestions(User user, Set<String> questionValues) {
        Query query = new Query();
        query.addCriteria(Criteria.where("value").nin(questionValues).and("category").in(user.getCategories()));
        return mongoTemplate.find(query, Question.class);
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    @Override
    public int count() {
        return mongoTemplate.findAll(User.class).size();
    }

}
