package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Authority;
import com.akulinski.keepmeawake.core.domain.AuthorityType;
import com.akulinski.keepmeawake.core.domain.Category;
import com.akulinski.keepmeawake.core.domain.User;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    public void save() {
        var current = userRepository.count();

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote());
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);

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
        user.setEmail(faker.yoda().quote());
        Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
        user.setCategories(categories);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        userRepository.save(user);

        assertTrue(userRepository.findByUsername(user.getUsername()).isPresent());
    }

    @Test
    public void findAll() {
        var current = userRepository.count();

        Stream.generate(() -> {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setPassword(faker.name().nameWithMiddle());
            user.setEmail(faker.yoda().quote());
            Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
            user.setCategories(categories);

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority(AuthorityType.USER));
            user.setAuthorities(authorities);

            return user;
        }).limit(10).forEach(userRepository::save);

        assertEquals(current + 10, userRepository.findAll().size());

    }

    @Test
    public void findById() {

        User user = new User();
        user.setUsername(faker.name().username());
        user.setPassword(faker.name().nameWithMiddle());
        user.setEmail(faker.yoda().quote());
        Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
        user.setCategories(categories);

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
        user.setEmail(faker.yoda().quote());
        Set<Category> categories = new HashSet<>(Arrays.asList(Category.values()));
        user.setCategories(categories);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        final var save = userRepository.save(user);

        assertTrue(userRepository.findById(save.getId()).isPresent());


        userRepository.deleteById(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
