package com.boefcity.projectmanagement.controller;


import com.boefcity.projectmanagement.config.AppUtility;
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
                redirectAttributes.addFlashAttribute("message", "Brugernavn eksisterer allerede. Brug et andet brugernavn");
                return "redirect:/users/registerDisplay";
            }
            System.out.println("for create");
            userService.createUser(user);
            System.out.println("efter create");
            redirectAttributes.addFlashAttribute("message", "Brugeren blev oprettet");
            return "redirect:/users/loginDisplay";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget gik galt. Prøv venligst igen");
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
                redirectAttributes.addFlashAttribute("message", "Forkert brugernavn eller kodeord. Prøv venligst igen");
                return "redirect:/users/loginDisplay";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget gik galt. Prøv venligst igen");
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/userListDisplay")
    public String userListDisplay(HttpSession session,
                                  Model model, RedirectAttributes redirectAttributes) {
        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        try {
            List<User> userList = userService.findAllUsers();
            model.addAttribute("userList", userList);
            return "/user/userList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Kunne ikke få fat på listen af brugere");
            return "redirect:/users/loginDisplay";
        }
    }
    @PostMapping("/delete/{userToDeleteId}")
    public String deleteUser(@PathVariable Long userToDeleteId, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
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
            redirectAttributes.addFlashAttribute("message", "Brugeren blev slettet");
        } else {
            redirectAttributes.addFlashAttribute("message", "Brugeren blev ikke fundet");
        }
        return "redirect:/users/userListDisplay";
    }

    // Ændring af user

    @GetMapping("/editDisplay/{userId}")
    public String editUser(@PathVariable Long userId,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan benytte denne funktion");
            return "redirect:/menu";
        }

        User userToEdit = userService.findUserById(userId);
        if (userToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Brugeren blev ikke fundet");
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

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan benytte denne funktion");
            return "redirect:/menu";
        }

        userService.editUser(userId, userDetails);
        redirectAttributes.addFlashAttribute("message", "Brugeren blev opdateret");
        return "redirect:/users/userListDisplay";
    }



}
