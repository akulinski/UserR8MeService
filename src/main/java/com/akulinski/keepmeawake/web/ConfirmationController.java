package com.akulinski.keepmeawake.web;

import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/activate")
public class ConfirmationController {

    private final UserRepository userRepository;

    public ConfirmationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{link}")
    public ResponseEntity confirmation(@PathVariable("link") String link) {

        User user = userRepository.findUserByLink(link).orElseThrow(() -> new IllegalArgumentException(String.format("No user connected to link: %s", link)));

        user.setIsEnabled(true);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
