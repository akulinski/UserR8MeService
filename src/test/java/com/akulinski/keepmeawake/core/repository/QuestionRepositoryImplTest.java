package com.akulinski.keepmeawake.core.repository;

import com.akulinski.keepmeawake.core.domain.Category;
import com.akulinski.keepmeawake.core.domain.Question;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionRepositoryImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuestionRepository questionRepository;

    private final Faker faker = new Faker();

    @BeforeAll
    public void setUp() {
        mongoTemplate.remove(Question.class);

        Stream.generate(() -> {
            Question question = new Question();
            question.setValue(faker.hobbit().quote());
            question.setDescription(faker.hobbit().thorinsCompany());
            question.setCategory(Category.GENERAL_KNOWLEDGE);
            return question;
        }).limit(10 - questionRepository.count()).forEach(questionRepository::save);
    }

    @AfterAll
    public void tearDown() {
        mongoTemplate.remove(Question.class);
    }

    @Test
    public void findAll() {

        var current = questionRepository.count();

        Stream.generate(() -> {
            Question question = new Question();
            question.setValue(faker.hobbit().quote());
            question.setDescription(faker.hobbit().thorinsCompany());
            question.setCategory(Category.GENERAL_KNOWLEDGE);
            return question;
        }).limit(2 - questionRepository.count()).forEach(questionRepository::save);

        assertEquals(current + 2, questionRepository.count());
    }

    @Test
    public void save() {
        var current = questionRepository.count();

        Stream.generate(() -> {
            Question question = new Question();
            question.setValue(faker.hobbit().quote());
            question.setDescription(faker.hobbit().thorinsCompany());
            question.setCategory(Category.GENERAL_KNOWLEDGE);
            return question;
        }).limit(1).forEach(questionRepository::save);


        assertEquals(current + 1, questionRepository.count());

    }

    @Test
    public void deleteById() {

        Question question = new Question();
        question.setValue(faker.hobbit().quote());
        question.setDescription(faker.hobbit().thorinsCompany());
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        final var save = questionRepository.save(question);

        assertTrue(questionRepository.findById(save.getId()).isPresent());

        questionRepository.deleteById(save.getId());

        assertFalse(questionRepository.findById(save.getId()).isPresent());
    }

    @Test
    public void findById() {
        Question question = new Question();
        question.setValue(faker.hobbit().quote());
        question.setDescription(faker.hobbit().thorinsCompany());
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        final var save = questionRepository.save(question);

        assertTrue(questionRepository.findById(save.getId()).isPresent());
    }

}
