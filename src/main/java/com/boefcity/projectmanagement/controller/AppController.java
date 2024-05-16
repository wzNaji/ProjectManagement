package com.boefcity.projectmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @GetMapping("/")
    public String homepageDisplay() {
        return "homepage";
    }

    @GetMapping("/menu")
    public String menuPageDisplay() {
        return "menuPage";
    }

    @GetMapping("/errorPage") // Kan ikke få expection message til at virke
    public String errorPageDisplay(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "hm" + exception.getMessage());
        return "errorPage";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "You have been logged out");
        return "redirect:/users/loginDisplay";
    }
}
