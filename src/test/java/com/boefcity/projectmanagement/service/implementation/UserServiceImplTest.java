package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRole(Role.ADMIN);
    }

    @Test
    void testFindUserById() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(user);

        User foundUser = userService.findUserById(1L);

        assertEquals(user, foundUser);
    }

    @Test
    void testFindUserById_NotFound() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.findUserById(1L));
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(Role.ADMIN, createdUser.getUserRole());
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEditUser() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(user);

        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setUserRole(Role.MANAGER);

        userService.editUser(1L, updatedUser);

        verify(userRepository, times(1)).save(user);
        assertEquals("updatedUser", user.getUsername());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals(Role.MANAGER, user.getUserRole());
    }

    @Test
    void testEditUser_NotFound() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(null);

        User updatedUser = new User();

        assertThrows(EntityNotFoundException.class, () -> userService.editUser(1L, updatedUser));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testFindAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAllUsers();

        assertEquals(users, foundUsers);
    }

    @Test
    void testCheckLogin_ValidUser() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);

        boolean isValid = userService.checkLogin("testuser", "password");

        assertTrue(isValid);
    }

    @Test
    void testCheckLogin_InvalidUser() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        boolean isValid = userService.checkLogin("testuser", "wrongpassword");

        assertFalse(isValid);
    }

    @Test
    void testFindUserByUsername() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void testFindUserByUsername_NotFound() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findUserByUsername("testuser");

        assertFalse(foundUser.isPresent());
    }
}
