package com.akulinski.userr8meservice.aop;

import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.ChangePasswordDTO;
import com.akulinski.userr8meservice.core.domain.dto.CreateUserDTO;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidEmailException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidPasswordException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidUsernameException;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import com.akulinski.userr8meservice.core.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationAspectTest {

    @Autowired
    private UserRepository userRepository;

    private RedisServer redisServer;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {

        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @After
    public void tearDown() throws Exception {
        redisServer.stop();
    }

    @Test(expected = InvalidEmailException.class)
    public void validateInvalidEmail() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("adfadsfadsf.com");
        user.setPassword("asFDSAasaaa");
        user.setUsername("test1");
        final var save = userRepository.save(user);
        assertNull(save);
    }

    public void checkIfInvlaidPreventsDbSave() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("adfadsfadsf.com");
        user.setPassword("asFDSAasaaa");
        user.setUsername("test1");
        final var save = userRepository.save(user);
        assertNull(save);
        assertTrue(userRepository.findByUsername("test1").isEmpty());
    }

    @Test
    public void validateValidEmail() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("ab.k@email.com");
        user.setPassword("asFDSAasaaa");
        user.setUsername("test1");
        final var save = userRepository.save(user);
        assertNotNull(save);
    }

    @Test(expected = InvalidUsernameException.class)
    public void validateInvalidUsername() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("adfadsfadsf@email.com");
        user.setPassword("asFDSAasaaa");
        user.setUsername("tes");
        final var save = userRepository.save(user);
        assertNull(save);
    }

    @Test
    public void validateValidUsername() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("ab.k@email.com");
        user.setPassword("asFDSAasaaa");
        user.setUsername("test1");
        final var save = userRepository.save(user);
        assertNotNull(save);
    }

    @Test(expected = InvalidPasswordException.class)
    public void validateInvalidPassword() {
        userRepository.deleteAll();
        var userDTO = new CreateUserDTO("testtttt", "t", "test@test.com");
        final var user = userService.getUser(userDTO);
        assertNull(user);
    }

    @Test(expected = InvalidPasswordException.class)
    public void checkIfFailedValidationPreventsDBSave() {
        userRepository.deleteAll();
        var userDTO = new CreateUserDTO("testtttt", "t", "test@test.com");
        final var user = userService.getUser(userDTO);
        assertNull(user);

        assertTrue(userRepository.findByUsername("testtttt").isEmpty());
    }

    @Test
    public void validateValidPassword() {
        userRepository.deleteAll();
        var userDTO = new CreateUserDTO("testtttt", "TeEsT1!2@", "test@test.com");
        final var user = userService.getUser(userDTO);
        assertNotNull(user);
    }

    @Test(expected = InvalidPasswordException.class)
    public void validatePasswordChange() {
        userRepository.deleteAll();
        var userDTO = new CreateUserDTO("testtttt", "TeEsT1!2@", "test@test.com");
        userService.getUser(userDTO);

        var changePasswordDTO = new ChangePasswordDTO("TeEsT1!2@", "password");
        userService.changePassword("testtttt", changePasswordDTO);
    }

    @Test
    public void validateValidPasswordChange() {
        userRepository.deleteAll();
        var userDTO = new CreateUserDTO("testtttt", "TeEsT1!2@", "test@test.com");
        userService.getUser(userDTO);

        var changePasswordDTO = new ChangePasswordDTO("TeEsT1!2@", "TeEsT1!2@11");
        userService.changePassword("testtttt", changePasswordDTO);

        final var user = userRepository.findByUsername("testtttt").get();
        passwordEncoder.matches("TeEsT1!2@11", user.getPassword());
    }
}
