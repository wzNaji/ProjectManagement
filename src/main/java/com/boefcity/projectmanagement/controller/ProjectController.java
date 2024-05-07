package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/display")
    public String projectsDisplay(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }

        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role)) {
            List<Project> adminProjectList = projectService.findAll();
            model.addAttribute("projectList", adminProjectList);
            return "adminProjectList";
        } else if (Role.WORKER.equals(role) || Role.MANAGER.equals(role)) {
            List<Project> projectList = user.getProjects();
            model.addAttribute("projectList", projectList);
            return "userProjectList";
        }
        return "errorPage";
    }
}
