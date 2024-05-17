package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
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
    public String menuPageDisplay(HttpSession session, RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        return "menuPage";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "You have been logged out");
        return "redirect:/users/loginDisplay";
    }
}
