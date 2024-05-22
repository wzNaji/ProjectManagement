package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Test for at sikre, at bruger findes ved hjælp af native SQL query
    @Test
    public void testFindUserByIdNative() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRole(Role.MANAGER);

        user = userRepository.save(user);

        User foundUser = userRepository.findUserByIdNative(user.getUserId());
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    // Test for at sikre, at bruger findes ved hjælp af brugernavn
    @Test
    public void testFindUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRole(Role.MANAGER);

        user = userRepository.save(user);

        Optional<User> foundUser = userRepository.findUserByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    // Test for at sikre, at bruger ikke findes ved hjælp af ugyldigt brugernavn
    @Test
    public void testFindUserByInvalidUsername() {
        Optional<User> foundUser = userRepository.findUserByUsername("invaliduser");
        assertFalse(foundUser.isPresent());
    }
}
