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

    @PostMapping("/delete")
    public String deleteTask(@RequestParam("taskId") Long taskId,
                             @RequestParam("subprojectId") Long subprojectId,
                             @RequestParam("projectId") Long projectId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);
        if (!AppUtility.isAdminOrManager(user)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere har tilladelse til sletning");
            return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
        }

        try {
            Task taskToDelete = taskService.findByTaskId(taskId);
            if (taskToDelete == null) {
                redirectAttributes.addFlashAttribute("message", "Task blev ikke fundet");
            } else {
                taskService.deleteTask(taskId, subprojectId);
                redirectAttributes.addFlashAttribute("message", "Task blev slettet");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Fejl under sletning af task");
        }

        return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
    }

    @GetMapping("/editFormDisplay/{taskId}")
    public String editTaskFormDisplay(@PathVariable Long taskId,
                                      @RequestParam Long projectId,
                                      @RequestParam Long subprojectId,
                                      Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        Project project = projectService.findProjectById(projectId);
        Subproject subproject = subprojectService.findBySubprojectId(subprojectId);
        Task taskToEdit = taskService.findByTaskId(taskId);
        if (taskToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Task til redigering blev ikke fundet");
            return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
        }

        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("task", taskToEdit);
        model.addAttribute("project", project);
        model.addAttribute("subproject", subproject);
        model.addAttribute("task", taskToEdit);
        return "project/subproject/task/taskEditForm";
    }

    @PostMapping("/editForm/{taskId}")
    public String editTask(@PathVariable Long taskId,
                           @RequestParam Long subprojectId,
                           @RequestParam Long projectId,
                           @ModelAttribute("task") Task taskDetails,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Task existingTask = taskService.findByTaskId(taskId);
        if (existingTask == null) {
            redirectAttributes.addFlashAttribute("message", "Task til redigering blev ikke fundet");
            return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
        }

        try {
            taskService.editTask(taskId, taskDetails);
            redirectAttributes.addFlashAttribute("message", "Task blev opdateret");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget gik galt. Prøv venligst igen.");
        }

        return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
    }

    @PostMapping("/delete")
    public String deleteTask(@RequestParam("taskId") Long taskId,
                             @RequestParam("subprojectId") Long subprojectId,
                             @RequestParam("projectId") Long projectId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);
        if (!AppUtility.isAdminOrManager(user)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere har tilladelse til sletning");
            return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
        }

        try {
            Task taskToDelete = taskService.findByTaskId(taskId);
            if (taskToDelete == null) {
                redirectAttributes.addFlashAttribute("message", "Task blev ikke fundet");
            } else {
                taskService.deleteTask(taskId, subprojectId);
                redirectAttributes.addFlashAttribute("message", "Task blev slettet");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Fejl under sletning af task");
        }

        return "redirect:/subprojects/overviewDisplay?subprojectId=" + subprojectId + "&projectId=" + projectId;
    }

}
