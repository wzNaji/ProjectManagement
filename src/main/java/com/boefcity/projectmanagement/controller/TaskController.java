package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.TaskService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, UserService userService, ProjectService projectService) {
        this.taskService = taskService;
        this.userService = userService;
        this.projectService = projectService;
    }


    @GetMapping("/display")
    public String tasksDisplay(@RequestParam Long projectId, HttpSession session, Model model,
                               RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            List<Task> adminTaskList = taskService.findAllTask();
            model.addAttribute("taskList", adminTaskList);
            return "/project/editDisplay";
        } else if (Role.WORKER.equals(role)) {
            List<Task> userTaskList = user.getTasks();
            model.addAttribute("taskList", userTaskList);
            return "/project/editDisplay";
        }
        return "errorPage";
    }

    @GetMapping("/addFormDisplay")
    public String addFormDisplay(@RequestParam("projectId") Long projectId, Model model, HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);
        Project projectToFind = projectService.findById(projectId);

        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            model.addAttribute("task", new Task());
            model.addAttribute("project", projectToFind);
            model.addAttribute("priorityLevel", PriorityLevel.values());
            model.addAttribute("status", Status.values());
            return "/project/task/taskAddForm";
        } else if (Role.WORKER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to add new tasks");
            return "redirect:/projects/display";
        }
        return "/errorPage";
    }

    @PostMapping("/addForm")
    public String createAndAssignTask(@RequestParam("projectId") Long projectId,
                                      @ModelAttribute Task task,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        System.out.println();
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);


        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            try {

                taskService.createTask(task);
                projectService.assignTaskToProject(task, projectId);

                redirectAttributes.addFlashAttribute("message",
                        "You have successfully created a new task: " + task.getTaskName());
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Something went wrong. Please try again.");
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            }
        }

        return "errorPage";
    }


}

