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

    // Delete task

    @PostMapping("/delete")
    public String deleteTask(@RequestParam("taskId") Long taskId,
                             @RequestParam("projectId") Long projectId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);
        Role role = user.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "You are not authorized to delete tasks");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        try {
            Task taskToDelete = taskService.findByTaskId(taskId);
            if (taskToDelete != null) {
                taskService.deleteTask(taskId, projectId);
                redirectAttributes.addFlashAttribute("message",
                        "You have successfully deleted task: " + taskToDelete.getTaskName());
            } else {
                redirectAttributes.addFlashAttribute("message", "Task not found.");
            }
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting task");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }
    }

    // edit task

    @GetMapping("/editFormDisplay/{taskId}")
    public String editUser(@PathVariable Long taskId,
                           @RequestParam Long projectId,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit tasks");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Task taskToEdit = taskService.findByTaskId(taskId);
        if (taskToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Task to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("task", taskToEdit);
        model.addAttribute("projectId", projectId);
        return "/project/task/taskEditForm";
    }

    @PostMapping("/editForm/{taskId}")
    public String updateTask(@PathVariable Long taskId,
                             @RequestParam Long projectId,
                             @ModelAttribute("task") Task taskDetails,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit tasks");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Task existingTask = taskService.findByTaskId(taskId);
        if (existingTask == null) {
            redirectAttributes.addFlashAttribute("message", "Task to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }


        taskService.updateTask(taskId, taskDetails);

        redirectAttributes.addFlashAttribute("message", "Task updated successfully");
        return "redirect:/projects/editDisplay?projectId=" + projectId;
    }


}

