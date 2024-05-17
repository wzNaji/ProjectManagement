package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubprojectService;
import com.boefcity.projectmanagement.service.TaskService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final SubprojectService subprojectService;
    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public TaskController(SubprojectService subprojectService,
                          UserService userService,
                          ProjectService projectService,
                          TaskService taskService) {

        this.subprojectService = subprojectService;
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
    }


    @GetMapping("/addFormDisplay")
    public String taskAddFormDisplay(@RequestParam("subprojectId") Long subprojectId,
                                     @RequestParam("projectId") Long projectId,
                                     Model model, HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        Project project = projectService.findProjectById(projectId);
        Subproject subproject = subprojectService.findBySubprojectId(subprojectId);
        if (subproject == null) {
            redirectAttributes.addFlashAttribute("message", "Subprojektet blev ikke fundet.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;

        }
        model.addAttribute("task", new Task());
        model.addAttribute("subproject", subproject);
        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("project", project);

        return "/project/subproject/task/taskAddForm";
    }

    @PostMapping("/addForm")
    public String createAndAssignTask(@RequestParam("subprojectId") Long subprojectId,
                                      @RequestParam("projectId") Long projectId,
                                      @ModelAttribute Task task,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        try {
            taskService.createTask(task);
            subprojectService.assignTaskToSubproject(task, subprojectId);

            redirectAttributes.addFlashAttribute("message", "Tasken er oprettet");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget galt. Prøv venligst igen.");
        }

        return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;

    }

}
