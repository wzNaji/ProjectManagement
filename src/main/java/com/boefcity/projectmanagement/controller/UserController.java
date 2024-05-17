package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/loginDisplay")
    public String loginDisplay() {
        return "loginPage";
    }
    @GetMapping("/registerDisplay")
    public String registerDisplay(Model model) {
        model.addAttribute("user", new User());
        return "registerPage";
    }
    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute User user,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<User> optionalExistingUser = userService.findUserByUsername(user.getUsername());
            if (optionalExistingUser.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Username already exists.");
                return "redirect:/users/registerDisplay";
            }
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("message", "User registered successfully!");
            return "redirect:/users/loginDisplay";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred.");
            return "redirect:/users/registerDisplay";
        }
    }

    @PostMapping("/loginUser")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            boolean isValidUser = userService.checkLogin(username, password);
            if (isValidUser) {
                Optional<User> optionalUser = userService.findUserByUsername(username);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    session.setAttribute("userId", user.getUserId());
                    return "redirect:/menu";
                }
                return "redirect:/errorPage";
            } else {
                redirectAttributes.addFlashAttribute("message", "Incorrect username or password. Try again");
                return "redirect:/users/loginDisplay";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred.");
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/userListDisplay")
    public String userListDisplay(HttpSession session,
                                  Model model, RedirectAttributes redirectAttributes) {
        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
// worker skal ikke kunne se alle brugere
        try {
            List<User> userList = userService.findAllUsers();
            model.addAttribute("userList", userList);
            return "userList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while fetching the user list.");
            return "redirect:/users/loginDisplay";
        }
    }
    @PostMapping("/delete/{userToDeleteId}")
    public String deleteUser(@PathVariable Long userToDeleteId, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        Role role = currentUser.getUserRole();
        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            return "errorPage";
        }

        User userToDelete = userService.findUserById(userToDeleteId);
        if (userToDelete != null) {
            userService.deleteUser(userToDeleteId);
            redirectAttributes.addFlashAttribute("message",
                    userToDelete.getUsername() + " was successfully deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found.");
        }
        return "redirect:/users/userListDisplay";
    }

    // Ændring af user

    @GetMapping("/editDisplay/{userId}")
    public String editUser(@PathVariable Long userId,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to see user list");
            return "redirect:/menu";
        }

        User userToEdit = userService.findUserById(userId);
        if (userToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "User to edit was not found");
            return "redirect:/menu";
        }

        model.addAttribute("roles", Role.values());
        model.addAttribute("user", userToEdit);
        return "/user/userEditForm";
    }

    @PostMapping("/editForm/{userId}")
    public String editUser(@PathVariable Long userId,
                             @ModelAttribute("user") User userDetails,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit user");
            return "redirect:/menu";
        }

        userService.editUser(userId, userDetails);
        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/users/userListDisplay";
    }



}
