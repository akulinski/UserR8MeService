package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Authority;
import com.akulinski.userr8meservice.core.domain.AuthorityType;
import com.akulinski.userr8meservice.core.domain.Rate;
import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.ChangePasswordDTO;
import com.akulinski.userr8meservice.core.domain.dto.RateDTO;
import com.akulinski.userr8meservice.core.domain.dto.UserDTO;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
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

    private String getLinkThatIsNotPresent(String id) {
        return RandomStringUtils.randomAlphanumeric(30) + id;
    }


    public User getUser(UserDTO userDTO) {
        User user = mapDTO(userDTO);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        String link = getLinkThatIsNotPresent(user.getId());

        user.setLink(link);

        userRepository.save(user);

        return user;
    }

    public User changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        final var byUsername = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username)));

        if (byUsername.getPassword().equals(passwordEncoder.encode(changePasswordDTO.getOldPassword()))) {
            byUsername.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(byUsername);
        }

        return byUsername;
    }


    public void addRateToUser(@RequestBody RateDTO rateDTO, User toRate, User rater) {
        Rate rate = new Rate();
        rate.setRate(rateDTO.getRating());
        rate.setSender(rater.getUsername());
        toRate.getRates().add(rate);
    }


    @Cacheable(cacheNames = "rating", key = "#byUsername.id")
    public Integer getSum(User byUsername) {
        final var sum = byUsername.getRates().stream()
                .map(Rate::getRate).reduce(Integer::sum).orElse(-1);
        byUsername.setCurrentRating(sum);

        userRepository.save(byUsername);
        return sum;
    }
}
