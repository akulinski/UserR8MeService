package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.domain.Authority;
import com.akulinski.userr8meservice.core.domain.AuthorityType;
import com.akulinski.userr8meservice.core.domain.dto.ChangePasswordDTO;
import com.akulinski.userr8meservice.core.domain.dto.UserDTO;
import com.akulinski.userr8meservice.core.repository.UserRepository;
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
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    private RedisServer redisServer;

    @Before
    public void setUp() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @After
    public void tearDown() throws Exception {
        redisServer.stop();
    }

    @Test
    public void mapDTONotNull() {
        UserDTO userDTO = new UserDTO("test","test","test@test.com");
        final var user = userService.mapDTO(userDTO);
        assertNotNull(user);
    }

    @Test
    public void mapDTOCorrectValues() {
        UserDTO userDTO = new UserDTO("test","test","test@test.com");
        final var user = userService.mapDTO(userDTO);
        assertEquals("test",user.getUsername());
    }

    @Test
    public void mapDTOPasswordIsHashed() {
        UserDTO userDTO = new UserDTO("test","test","test@test.com");
        final var user = userService.mapDTO(userDTO);
        assertEquals("test",user.getUsername());
        assertTrue(passwordEncoder.matches("test", user.getPassword()));
    }

    @Test
    public void getUserByUsernameNotNull() {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO("testttt","testT1!","test@test.com");
        userRepository.save(userService.mapDTO(userDTO));
        assertTrue(userRepository.findByUsername("testttt").isPresent());
    }

    @Test
    public void getUserByUsernameCorrectPassword() {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO("testtttt","test","test@test.com");
        userRepository.save(userService.mapDTO(userDTO));
        assertTrue(passwordEncoder.matches("test", userRepository.findByUsername("testtttt").get().getPassword()));
    }

    @Test
    public void getUser() {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO("testttt","testT1!1","test@test.com");
        userService.getUser(userDTO);
        assertTrue(userRepository.findByUsername("testttt").isPresent());
        assertEquals(1, userRepository.findByUsername("testttt").get().getAuthorities().stream().filter(auth -> ((Authority) auth).getAuthorityType() == AuthorityType.USER).count());
    }

    @Test
    public void changePassword() {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO("testtttt","testT1!","test@test.com");
        final var user = userService.getUser(userDTO);
        assertTrue(passwordEncoder.matches("testT1!", user.getPassword()));
        final var changePassword = userService.changePassword("testtttt", new ChangePasswordDTO("testT1!", "testT111")); //test validation during change
        assertFalse(passwordEncoder.matches("test", changePassword.getPassword()));
        assertTrue(passwordEncoder.matches("testT111", changePassword.getPassword()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void changePasswordThatDosntMatch() {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO("testtttt","testT1!","test@test.com");
        final var user = userService.getUser(userDTO);
        assertTrue(passwordEncoder.matches("testT1!", user.getPassword()));
        userService.changePassword("testtttt", new ChangePasswordDTO("testesdafdsa", "testT1qq"));
    }
    @Test
    public void addRateToUser() {
    }

    @Test
    public void createAndSaveComment() {
    }

    @Test
    public void deleteByName() {
    }

    @Test
    public void getById() {
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void getAll() {
    }

    @Test
    public void getSum() {
    }

    @Test
    public void followUser() {
    }

    @Test
    public void unFollowUser() {
    }
}