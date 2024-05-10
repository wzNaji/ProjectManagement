package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.UserService;
import com.boefcity.projectmanagement.config.SessionUtility;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    // Navigation tests
    @Test
    public void testLoginDisplay() {
        String viewName = userController.loginDisplay();
        assertEquals("loginPage", viewName);
    }

    @Test
    public void testRegisterDisplay() {
        when(model.addAttribute(eq("user"), any(User.class))).thenReturn(model);
        String viewName = userController.registerDisplay(model);
        assertEquals("registerPage", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    // User Registration Tests
    @Test
    public void testRegisterUser_UserExists() {
        User newUser = new User();
        newUser.setUsername("existingUser");
        when(userService.findUserByUsername("existingUser")).thenReturn(Optional.of(newUser));

        String result = userController.registerUser(newUser, redirectAttributes);
        assertEquals("redirect:/users/registerDisplay", result);
        verify(redirectAttributes).addFlashAttribute("message", "Username already exists.");
    }

    @Test
    public void testRegisterUser_Success() {
        User newUser = new User();
        newUser.setUsername("newUser");

        when(userService.findUserByUsername("newUser")).thenReturn(Optional.empty());
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        String result = userController.registerUser(newUser, redirectAttributes);

        assertEquals("redirect:/users/loginDisplay", result);
        verify(redirectAttributes).addFlashAttribute("message", "User registered successfully!");

        verify(userService).createUser(any(User.class));
    }

    @Test
    public void testRegisterUser_Exception() {
        User newUser = new User();
        newUser.setUsername("newUser");

        when(userService.findUserByUsername("newUser")).thenThrow(new RuntimeException("Database error"));

        String result = userController.registerUser(newUser, redirectAttributes);

        assertEquals("redirect:/users/registerDisplay", result);
        verify(redirectAttributes).addFlashAttribute("message", "An error occurred.");
    }

    // User Login Tests
    @Test
    public void testLoginUser_InvalidCredentials() {
        when(userService.checkLogin("testUser", "testPass")).thenReturn(false);

        String result = userController.loginUser("testUser", "testPass", session, redirectAttributes);
        assertEquals("redirect:/users/loginDisplay", result);
        verify(redirectAttributes).addFlashAttribute("message", "Incorrect username or password. Try again");
    }

    @Test
    public void testLoginUser_ValidCredentials() {
        when(userService.checkLogin("validUser", "validPass")).thenReturn(true);
        User user = new User();
        user.setUserId(1L);
        when(userService.findUserByUsername("validUser")).thenReturn(Optional.of(user));

        String result = userController.loginUser("validUser", "validPass", session, redirectAttributes);
        assertEquals("redirect:/menu", result);
        verify(session).setAttribute("userId", user.getUserId());
    }

    @Test
    public void testLoginUser_ValidUserNotFound() {
        when(userService.checkLogin("validUser", "validPass")).thenReturn(true);
        when(userService.findUserByUsername("validUser")).thenReturn(Optional.empty());

        String result = userController.loginUser("validUser", "validPass", session, redirectAttributes);
        assertEquals("redirect:/errorPage", result);
    }

    @Test
    public void testLoginUser_Exception() {
        when(userService.checkLogin("validUser", "validPass")).thenThrow(new RuntimeException("Service error"));

        String result = userController.loginUser("validUser", "validPass", session, redirectAttributes);
        assertEquals("redirect:/errorPage", result);
        verify(redirectAttributes).addFlashAttribute("message", "An unexpected error occurred.");
    }

    // User List Display Tests
    @Test
    public void testUserListDisplay_NotAuthenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = userController.userListDisplay(session, model, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testUserListDisplay_Authenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            List<User> userList = new ArrayList<>();
            userList.add(new User());
            when(userService.findAllUsers()).thenReturn(userList);

            String result = userController.userListDisplay(session, model, redirectAttributes);
            assertEquals("userList", result);
            verify(model).addAttribute("userList", userList);
        }
    }

    @Test
    public void testUserListDisplay_Exception() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            when(userService.findAllUsers()).thenThrow(new RuntimeException("Service error"));

            String result = userController.userListDisplay(session, model, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
            verify(redirectAttributes).addFlashAttribute("message", "An error occurred while fetching the user list.");
        }
    }
}
