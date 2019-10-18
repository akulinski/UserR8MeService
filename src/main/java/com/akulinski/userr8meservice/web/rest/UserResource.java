package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.ChangePasswordDTO;
import com.akulinski.userr8meservice.core.domain.dto.CommentDTO;
import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.domain.dto.UserDTO;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.core.services.EmailService;
import com.akulinski.userr8meservice.core.services.UserService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.function.Supplier;

/**
 * User related endpoints
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserResource {

    private final EmailService emailService;

    private final UserService userService;

    private final UserRepository userRepository;

    public UserResource(EmailService emailService, UserService userService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Creates new user, no mail
     * confirmation implemented yet
     *
     * @param userDTO
     * @return
     */
    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) throws UnirestException {

        User user = userService.getUser(userDTO);

        emailService.sendMessage(user.getEmail(), user.getUsername(), user.getLink(), "All Best, KeepMeAwake Team");
        return ResponseEntity.ok(user);
    }

    /**
     * Returns user profile based
     * on jwt token
     *
     * @param principal
     * @return
     */
    @GetMapping
    public ResponseEntity<User> getCurrentProfile(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(String.format("No user with username: %s", principal.getName())));
        return ResponseEntity.ok(user);
    }

    /**
     * Change password endpoint
     * when current password matches old password
     * from changepassworddto new password is set
     *
     * @param principal
     * @param changePasswordDTO
     * @return
     */
    @PutMapping
    public ResponseEntity<User> changePassword(Principal principal, @RequestBody ChangePasswordDTO changePasswordDTO) {
        final var name = principal.getName();

        User user = userService.changePassword(name, changePasswordDTO);

        return ResponseEntity.ok(user);
    }


    @PostMapping("/rate")
    public ResponseEntity rateUser(@RequestBody RateDTO rateDTO, Principal principal) {
        final var toRate = userRepository.findByUsername(rateDTO.getReceiver()).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", rateDTO.getReceiver()));
        final var rater = userRepository.findByUsername(principal.getName()).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", principal.getName()));

        userService.addRateToUser(rateDTO, toRate, rater);

        return ResponseEntity.ok(toRate);
    }

    @PostMapping("/comment")
    public ResponseEntity commentUser(@RequestBody CommentDTO commentDTO, Principal principal) {

        final var receiver = userRepository.findByUsername(commentDTO.getReceiver())
                .orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", commentDTO.getReceiver()));

        final var poster = userRepository.findByUsername(principal.getName())
                .orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", principal.getName()));

        Comment comment = createAndSaveComment(commentDTO, receiver, poster);

        return ResponseEntity.ok(comment);
    }

    private Comment createAndSaveComment(@RequestBody CommentDTO commentDTO, User receiver, User poster) {
        Comment comment = new Comment();
        comment.setComment(commentDTO.getComment());
        comment.setCommenter(poster.getUsername());

        receiver.getComments().add(comment);
        userRepository.save(receiver);
        return comment;
    }

    @GetMapping("/get-rating")
    public ResponseEntity getRating(Principal principal) {
        final var byUsername = userRepository.findByUsername(principal.getName())
                .orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", principal.getName()));

        final Integer sum = userService.getSum(byUsername);
        return ResponseEntity.ok(sum);
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


    /**
     * Returns all users
     * Admin authority is needed
     * for user
     *
     * @return
     */
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
    @CacheEvict(cacheNames = "users")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", username)));
    }

}
