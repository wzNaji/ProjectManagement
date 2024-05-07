package com.boefcity.projectmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/errorPage")
    public String errorPageDisplay() {
        return "errorPage";
    }
}
