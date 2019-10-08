package com.akulinski.keepmeawake.web.rest;

import com.akulinski.keepmeawake.core.domain.Authority;
import com.akulinski.keepmeawake.core.domain.AuthorityType;
import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.domain.dto.UserDTO;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import com.akulinski.keepmeawake.core.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
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
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<User> getCurrentProfile(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(String.format("No user with username: %s", principal.getName())));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/questions/random")
    public ResponseEntity getQuestionForCurrentUser(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", principal.getName()));

        Question chosenQuestion = getQuestion(user);

        return ResponseEntity.ok(chosenQuestion);
    }


    @DeleteMapping
    @CacheEvict(cacheNames = "users")
    public ResponseEntity deleteCurrentUser(Principal principal) {
        final var id = userRepository.findByUsername(principal.getName()).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", principal.getName())).getId();

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Supplier<IllegalArgumentException> getIllegalArgumentExceptionSupplier(String s, String name) {
        return () -> new IllegalArgumentException(String.format(s, name));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/id/{id}")
    public ResponseEntity findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(userRepository.findById(id).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by id %s", id)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/id/{id}")
    @CacheEvict(cacheNames = "users ")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", username)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/questions/{username}")
    public ResponseEntity getUserQuestions(@PathVariable("username") String username) {
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", username)).getAskedQuestions());
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/questions/random/{username}")
    public ResponseEntity getQuestionForUser(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", username));
        Question chosenQuestion = getQuestion(user);

        return ResponseEntity.ok(chosenQuestion);
    }


    private Question getQuestion(User user) {
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
        return chosenQuestion;
    }

}
