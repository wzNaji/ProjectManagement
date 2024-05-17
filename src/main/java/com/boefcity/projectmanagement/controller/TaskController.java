package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubprojectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final SubprojectService subprojectService;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskController(SubprojectService subprojectService, UserService userService, ProjectService projectService) {
        this.subprojectService = subprojectService;
        this.userService = userService;
        this.projectService = projectService;
    }


    @GetMapping("/addFormDisplay")
    public String taskAddFormDisplay(@RequestParam("subprojectId") Long subprojectId, Model model, HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Subproject subprojectToFind = subprojectService.findBySubprojectId(subprojectId);
        if (subprojectToFind == null) {
            redirectAttributes.addFlashAttribute("message", "Task blev ikke fundet.");
            return "bsajd";
        }


        model.addAttribute("task", new Task());
        model.addAttribute("subproject", subprojectToFind);
        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());

        return "/project/subproject/task/taskAddForm";
    }

}
