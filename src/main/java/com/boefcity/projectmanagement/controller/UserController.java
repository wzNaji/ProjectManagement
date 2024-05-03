package com.boefcity.projectmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/loginDisplay")
    public String loginDisplay() {
        return "loginPage";
    }
    @GetMapping("/registerDisplay")
    public String registerDisplay() {
        return "registerPage";
    }

}
