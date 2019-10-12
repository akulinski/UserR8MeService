package com.akulinski.keepmeawake.core.services;

import com.akulinski.keepmeawake.core.domain.Authority;
import com.akulinski.keepmeawake.core.domain.AuthorityType;
import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.User;
import com.akulinski.keepmeawake.core.domain.dto.UserDTO;
import com.akulinski.keepmeawake.core.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User mapDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setIsEnabled(Boolean.FALSE);
        return user;
    }

    private String getLinkThatIsNotPresent() {
        var link = RandomStringUtils.randomAlphanumeric(30);
        boolean linkPresent = userRepository.isLinkPresent(link);

        while (linkPresent){
            link = RandomStringUtils.randomAlphanumeric(30);
            linkPresent = userRepository.isLinkPresent(link);
        }
        return link;
    }


    public User getUser(UserDTO userDTO) {
        User user = mapDTO(userDTO);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        String link = getLinkThatIsNotPresent();

        user.setLink(link);

        userRepository.save(user);

        return user;
    }

    public Question choseQuestion(User user, Set<String> questionValues) {

        List<Question> byValueNotInAndCategoryIn = userRepository.findQuestions(user, questionValues);

        if (byValueNotInAndCategoryIn.isEmpty()) {
            throw new IllegalStateException("No new questions for user");
        }

        final Question question = byValueNotInAndCategoryIn.get(0);

        user.getAskedQuestions().add(question);

        userRepository.save(user);

        return question;
    }


}
