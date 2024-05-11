package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.TaskService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String tasksDisplay(HttpSession session, Model model,
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
}
