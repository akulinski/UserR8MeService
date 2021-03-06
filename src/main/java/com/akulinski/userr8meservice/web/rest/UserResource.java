package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.*;
import com.akulinski.userr8meservice.core.services.EmailService;
import com.akulinski.userr8meservice.core.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

/**
 * User related endpoints
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserResource {

    private final EmailService emailService;

    private final UserService userService;

    public UserResource(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * Creates new user, no mail
     * confirmation implemented yet
     *
     * @param createUserDTO
     * @return
     */
    @PostMapping
    public ResponseEntity createUser(@RequestBody CreateUserDTO createUserDTO) {

        User user = userService.getUser(createUserDTO);

        emailService.sendMessage(user.getEmail(), user.getUsername(), user.getLink(), "All Best, KeepMeAwake Team");
        return ResponseEntity.created(URI.create(user.getUsername())).build();
    }

    /**
     * Returns user profile based
     * on jwt token
     *
     * @param principal
     * @return
     */
    @GetMapping
    public ResponseEntity<UserDTO> getCurrentProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserDTOByUsername(principal.getName()));
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
    @Deprecated
    public ResponseEntity rateUser(@RequestBody RateDTO rateDTO, Principal principal) {
        final var toRate = userService.getUserDTOByUsername(rateDTO.getReceiver());
        final var rater = userService.getUserDTOByUsername(principal.getName());

        userService.addRateToUser(rateDTO, toRate, rater);

        return ResponseEntity.ok(toRate);
    }

    @PostMapping("/comment")
    public ResponseEntity commentUser(@RequestBody CommentDTO commentDTO, Principal principal) {

        final var receiver = userService.getUserDTOByUsername(commentDTO.getReceiver());

        final var poster = userService.getUserDTOByUsername(principal.getName());

        Comment comment = userService.createAndSaveComment(commentDTO, receiver, poster);

        return ResponseEntity.ok(comment);
    }


    @GetMapping("/get-rating")
    @Deprecated
    public ResponseEntity getRating(Principal principal) {
        final var byUsername = userService.getUserDTOByUsername(principal.getName());

        final var sum = userService.getSum(byUsername);
        return ResponseEntity.ok(sum);
    }

    @GetMapping("/follow/{username}")
    public ResponseEntity follow(Principal principal, @PathVariable("username") String username) {
        userService.followUser(principal.getName(), username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/unfollow/{username}")
    public ResponseEntity unFollow(Principal principal, @PathVariable("username") String username) {
        userService.unFollowUser(principal.getName(), username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @CacheEvict(cacheNames = "users")
    public ResponseEntity deleteCurrentUser(Principal principal) {
        userService.deleteByName(principal.getName());

        return ResponseEntity.noContent().build();
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
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/id/{id}")
    public ResponseEntity findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/id/{id}")
    @CacheEvict(cacheNames = "users")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserDTOByUsername(username));
    }

    @GetMapping("/find/{regex}")
    public ResponseEntity findRegexPaged(@PathVariable("regex") String regex, @RequestParam(required = false) Integer page) {

        if(page == null){
            return ResponseEntity.ok(userService.findByRegex(regex));
        }

        return ResponseEntity.ok(userService.findByRegexPage(page, regex));
    }
}
