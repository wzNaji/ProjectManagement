package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubProjectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/subProjects")
public class SubProjectController {
    private final SubProjectService subProjectService;
    private final UserService userService;
    private final ProjectService projectService;

    public SubProjectController(SubProjectService subProjectService, UserService userService, ProjectService projectService) {
        this.subProjectService = subProjectService;
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
            model.addAttribute("subProject", new SubProject());
            model.addAttribute("project", projectToFind);
            model.addAttribute("priorityLevel", PriorityLevel.values());
            model.addAttribute("status", Status.values());
            return "/project/subProject/subProjectAddForm";
        } else if (Role.WORKER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to add new sub project");
            return "redirect:/projects/display";
        }
        return "/errorPage";
    }

    @PostMapping("/addForm")
    public String createAndAssignSubProject(@RequestParam("projectId") Long projectId,
                                      @ModelAttribute SubProject subProject,
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

                subProjectService.createSubProject(subProject);
                projectService.assignSubProjectToProject(subProject, projectId);

                redirectAttributes.addFlashAttribute("message",
                        "You have successfully created a new sub project: " + subProject.getSubProjectName());
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Something went wrong. Please try again.");
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            }
        }

        return "errorPage";
    }

    // Delete sub project

    @PostMapping("/delete")
    public String deleteSubProject(@RequestParam("subProjectId") Long subProjectId,
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
            redirectAttributes.addFlashAttribute("message", "You are not authorized to delete sub projects");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        try {
            SubProject subProjectToDelete = subProjectService.findBySubProjectId(subProjectId);
            if (subProjectToDelete != null) {
                subProjectService.deleteSubProject(subProjectId, projectId);
                redirectAttributes.addFlashAttribute("message",
                        "You have successfully deleted sub project: " + subProjectToDelete.getSubProjectName());
            } else {
                redirectAttributes.addFlashAttribute("message", "Sub project not found.");
            }
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting sub project");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }
    }

    // edit sub project

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
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit sub projects");
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

        SubProject existingSubProject = subProjectService.findBySubProjectId(subProjectId);
        if (existingSubProject == null) {
            redirectAttributes.addFlashAttribute("message", "SubProject to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }


        subProjectService.updateSubProject(subProjectId, subProjectDetails);

        redirectAttributes.addFlashAttribute("message", "Task updated successfully");
        return "redirect:/projects/editDisplay?projectId=" + projectId;
    }


}

