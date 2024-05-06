package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    /*
    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute User user,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<User> optionalExistingUser = userService.findUserByUsername(user.getUsername());
            if (optionalExistingUser.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Username already exists.");
                return "redirect:/users/registerDisplay";
            }
            user.setUserRole(Role.WORKER);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("message", "User registered successfully!");
            return "redirect:/users/loginDisplay";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred.");
            return "redirect:/users/registerDisplay";
        }
    }

     */
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        boolean isValidUser = userService.checkLogin(username, password);
        if (isValidUser) {
            Optional<User> optionalUser = userService.findUserByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                session.setAttribute("userId", user.getUserId());
                return "redirect:/projects/display";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Incorrect username or password. Try again");
            return "redirect:/users/loginDisplay";
        }
        return "redirect:/users/loginDisplay";
    }


}
