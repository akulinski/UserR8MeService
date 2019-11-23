package com.akulinski.userr8meservice.core.repository;

import com.akulinski.userr8meservice.core.domain.Authority;
import com.akulinski.userr8meservice.core.domain.AuthorityType;
import com.akulinski.userr8meservice.core.domain.User;
import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    private final Faker faker = new Faker();

    private RedisServer redisServer;

    @BeforeEach
    public void deleteAll(){
        userRepository.deleteAll();
    }
    @Before
    public void setUp() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @After
    public void tearDown() throws Exception {
        redisServer.stop();
    }

    @Test
    public void save() {
        var current = userRepository.count();
        AtomicInteger counter = new AtomicInteger();

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote()+counter.getAndIncrement()+"@email.com");
            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            return user;
        }).limit(1).forEach(userRepository::save);

        assertEquals(current + 1, userRepository.count());
    }

    @Test
    public void findByUsername() {

        User user = new User();
        user.setUsername(faker.name().username());
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail(faker.yoda().quote()+"@email.com");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        userRepository.save(user);

        assertTrue(userRepository.findByUsername(user.getUsername()).isPresent());
    }

    @Test
    public void findAll() {
        var current = userRepository.count();

        AtomicInteger counter = new AtomicInteger();

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote()+counter.get()+"@email.com");

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);
            counter.getAndIncrement();
            return user;
        }).limit(10).forEach(userRepository::save);

        assertEquals(current + 10, userRepository.findAll().size());

    }

    @Test
    public void findById() {

        User user = new User();
        user.setUsername(faker.name().username());
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail(faker.yoda().quote()+"@email.com");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        final var save = userRepository.save(user);

        assertTrue(userRepository.findById(save.getId()).isPresent());
    }

    @Test
    public void deleteById() {
        User user = new User();
        user.setUsername(faker.name().username());
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail(faker.yoda().quote()+"@email.com");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        final var save = userRepository.save(user);

        assertTrue(userRepository.findById(save.getId()).isPresent());


        userRepository.deleteById(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void removeAll(){
        User user = new User();
        user.setUsername(faker.name().username());
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail(faker.yoda().quote()+"@email.com");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);
        final var count = userRepository.count();
        userRepository.save(user);

        assertEquals(count+1, userRepository.count());

        userRepository.deleteAll();
        assertEquals(0, userRepository.count());
    }
}
