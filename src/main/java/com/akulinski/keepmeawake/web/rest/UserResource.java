package com.akulinski.keepmeawake.web.rest;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.domain.UserDTO;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import com.akulinski.keepmeawake.core.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserResource {

    private final UserService userService;

    private final UserRepository userRepository;

    private final Random random;

    public UserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        random = new Random();
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) {

        User user = userService.mapDTO(userDTO);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }


    @GetMapping("/all")
    public ResponseEntity getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("No user found by id %s", id))));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found by username %s", username))));
    }

    @GetMapping("/questions/{username}")
    public ResponseEntity getUserQuestions(@PathVariable("username") String username) {
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found by username %s", username))).getAskedQuestions());
    }


    @GetMapping("/questions/random/{username}")
    public ResponseEntity getQuestionForUser(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found by username %s", username)));
        Set<String> questionValues = user.getAskedQuestions().stream().map(Question::getValue).collect(Collectors.toSet());


        Question chosenQuestion;

        try {
            chosenQuestion = userService.choseQuestion(user, questionValues);
        } catch (IllegalStateException ex) {
            log.warn(ex.getMessage());
            chosenQuestion = user.getAskedQuestions().stream()
                    .skip(random.nextInt(user.getAskedQuestions().size() - 1))
                    .findAny().orElseThrow(() -> new IllegalStateException("User has no questions"));
        }

        return ResponseEntity.ok(chosenQuestion);
    }


}
