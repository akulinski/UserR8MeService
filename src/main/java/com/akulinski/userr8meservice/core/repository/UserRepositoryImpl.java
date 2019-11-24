package com.akulinski.userr8meservice.core.repository;

import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.RegexResponseElement;
import com.akulinski.userr8meservice.core.exceptions.repository.DuplicateValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of repository
 * due to cacheing problem
 */
@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    private final RedisTemplate<String, User> redisTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate, RedisTemplate redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    @CachePut(cacheNames = "users", key = "#user.id")
    @Override
    public User save(User user) throws DuplicateValueException {
        User save = mongoTemplate.save(user);

        if (save == null) {
            throw new DuplicateValueException(String.format("User with username: %s or email: %s exists", user.getUsername(), user.getEmail()));
        }
        return save;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User userRedis = null;
        try {
            userRedis = (User) redisTemplate.opsForHash().get("users", username);
        }catch (RuntimeException ex){
            log.warn(ex.getMessage());
        }
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


    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    public boolean isLinkPresent(String link) {
        Query query = new Query();
        query.addCriteria(Criteria.where("link").is(link));

        return mongoTemplate.findOne(query, User.class) != null;
    }

    @Override
    public Optional<User> findUserByLink(String link) {
        Query query = new Query();
        query.addCriteria(Criteria.where("link").is(link));

        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    @Override
    public void deleteAll() {
        mongoTemplate.dropCollection(User.class);
    }

    @Override
    public List<User> findAllUsersByRegex(String regex) {

        Query query = new Query();

        Criteria criteria = new Criteria();

        criteria.orOperator(Criteria.where("username").regex(regex),Criteria.where("email").regex(regex));

        query.addCriteria(criteria);

        return mongoTemplate.find(query, User.class);
    }

    @Override
    public Page<User> pageRegex(String regex, Pageable pageable) {
        Query query = new Query();

        Criteria criteria = new Criteria();

        criteria.orOperator(Criteria.where("username").regex(regex),Criteria.where("email").regex(regex));

        query.addCriteria(criteria);
        query.with(pageable);

        List<User> users = mongoTemplate.find(query, User.class);

        return PageableExecutionUtils.getPage(users, pageable, ()->mongoTemplate.count(query, User.class));
    }

    @Override
    public int count() {
        return mongoTemplate.findAll(User.class).size();
    }

}
