package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.*;
import com.akulinski.userr8meservice.core.domain.dto.*;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public UserDTO mapUser(User user){
        return new UserDTO(user.getUsername(), user.getPassword(), user.getEmail());
    }

    private String getLinkThatIsNotPresent(String id) {
        return RandomStringUtils.randomNumeric(30)+"||" + id;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(String.format("No user with username: %s", username)));
    }

    public User getUser(UserDTO userDTO) {
        User user = mapDTO(userDTO);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(AuthorityType.USER));
        user.setAuthorities(authorities);

        user = userRepository.save(user);

        String link = getLinkThatIsNotPresent(user.getId());

        user.setLink(link);

        userRepository.save(user);

        return user;
    }

    public User changePassword(String username, ChangePasswordDTO changePasswordDTO) throws IllegalArgumentException{
        final var byUsername = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("No user found with username: %s", username)));

        if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), byUsername.getPassword())) {
            byUsername.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(byUsername);
        }else{
            throw  new IllegalArgumentException("Old password does not match");
        }

        return byUsername;
    }


    public void addRateToUser(@RequestBody RateDTO rateDTO, User toRate, User rater) {
        Rate rate = new Rate();
        rate.setRate(rateDTO.getRating());
        rate.setSender(rater.getUsername());
        toRate.getRates().add(rate);

        Map<String, List<Rate>> grouped = toRate.getRates().stream().collect(Collectors.groupingBy(Rate::getQuestion));
        grouped.forEach((s, rates) -> {
            double average = rates.stream().mapToDouble(Rate::getRate).average().getAsDouble();
            toRate.getRatesMap().put(s, average);
        });

        userRepository.save(toRate);
    }

    public Comment createAndSaveComment(CommentDTO commentDTO, User receiver, User poster) {
        Comment comment = new Comment();
        comment.setComment(commentDTO.getComment());
        comment.setCommenter(poster.getUsername());

        receiver.getComments().add(comment);
        userRepository.save(receiver);
        return comment;
    }

    public void deleteByName(String username) {
        final var id = userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", username)).getId();

        userRepository.deleteById(id);
    }


    private Supplier<IllegalArgumentException> getIllegalArgumentExceptionSupplier(String s, String name) {
        return () -> new IllegalArgumentException(String.format(s, name));
    }

    @Cacheable(cacheNames = "by-id", key = "#id")
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by id %s", id));
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "all-users")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Cacheable(cacheNames = "rating", key = "#byUsername.id")
    public Double getSum(User byUsername) {

        final var average = byUsername.getRates().stream()
                .mapToDouble(Rate::getRate).average().orElse(-1.0);

        byUsername.setCurrentRating(average);

        userRepository.save(byUsername);
        return average;
    }

    public void followUser(String follower, String followed) {
        final var followerUser = userRepository.findByUsername(follower).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", follower));
        final var followedUser = userRepository.findByUsername(followed).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", followed));

        followedUser.getFollowers().add(mapUser(followerUser));
        userRepository.save(followedUser);

        followerUser.getFollowing().add(mapUser(followedUser));
        userRepository.save(followerUser);
    }

    public void unFollowUser(String follower, String followed) throws IllegalArgumentException {

        final var followerUser = userRepository.findByUsername(follower).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", follower));

        final var followedUser = userRepository.findByUsername(follower).orElseThrow(getIllegalArgumentExceptionSupplier("No user found by username %s", followed));

        if (followedUser.getFollowers().contains(mapUser(followerUser))) {
            followedUser.getFollowers().remove(mapUser(followerUser));
            userRepository.save(followedUser);
        } else {
            throw new IllegalArgumentException(String.format("User %s is not following %s", follower, followed));
        }

        if (followerUser.getFollowing().contains(mapUser(followedUser))) {
            followerUser.getFollowing().remove(mapUser(followedUser));
            userRepository.save(followerUser);
        } else {
            throw new IllegalArgumentException(String.format("User %s is not followed by %s", followed, follower));
        }
    }

    @Cacheable(cacheNames = "regex-response-paged")
    public List<RegexResponseElement> findByRegexPage(int page, String regex){

        Pageable pageable = PageRequest.of(page,5);
        return userRepository.pageRegex(regex, pageable).stream()
                .map(usr->new RegexResponseElement(usr.getId(),usr.getUsername(),regex)).collect(Collectors.toList());
    }

    public List<RegexResponseElement> findByRegex(String regex){
        return userRepository.findAllUsersByRegex(regex).stream()
                .map(user -> new RegexResponseElement(user.getId(),user.getUsername(),regex)).collect(Collectors.toList());
    }
}
