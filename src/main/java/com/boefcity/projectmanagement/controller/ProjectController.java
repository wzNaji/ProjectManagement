package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String projectsDisplay(HttpSession session, Model model,
                                  RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        List<Project> projectList;
        if (AppUtility.isAdminOrManager(currentUser)) {
            projectList = projectService.findAllProjects();
        } else {
            projectList = currentUser.getProjects();
        }

        model.addAttribute("projectList", projectList);
        return "/project/projectList";
    }

    @GetMapping("/addForm")
    public String projectsAddFormDisplay(Model model, HttpSession session,
                                         RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to add new projects.");
            return "redirect:/projects/display";
        }

        model.addAttribute("project", new Project());
        return "/project/projectAddForm";
    }


    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to create a new project.");
            return "redirect:/projects/display";
        }

        try {
            projectService.createProject(project);
            redirectAttributes.addFlashAttribute("message",
                    "You have successfully created a new project: " + project.getProjectName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Something went wrong. Please try again.");
        }

        return "redirect:/projects/display";
    }


    // OVERVIEW DISPLAY
    @GetMapping("/overviewDisplay")
    public String projectsOverviewDisplay(HttpSession session, Model model, @RequestParam Long projectId,
                                    RedirectAttributes redirectAttributes) {
        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Project project;
        try {
            project = projectService.findProjectById(projectId);
            if (project == null) {
                redirectAttributes.addFlashAttribute("message", "Project not found.");
                return "redirect:/projects/display";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error retrieving project. Please try again.");
            return "redirect:/projects/display";
        }

        List<User> allUsers = userService.findAllUsers();
        List<User> assignedUsers = project.getUsers();
        List<Subproject> projectSubprojects = project.getSubprojects(); // brug service til at kalde

        model.addAttribute("project", project);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("assignedUsers", assignedUsers);
        model.addAttribute("projectSubprojects", projectSubprojects);

        return "project/projectsOverviewDisplay";
    }

    @PostMapping("/assignUser")
    public String assignUserToProject(@RequestParam Long projectId, @RequestParam Long userId,
                                      HttpSession session, RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        if (projectId == null || userId == null) {
            redirectAttributes.addFlashAttribute("message", "User or project information is missing. Please try again.");
            return "redirect:/projects/display";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to assign users to this project.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        try {
            if (projectService.isUserAssignedToProject(projectId, userId)) {
                redirectAttributes.addFlashAttribute("message", "User is already assigned to the project.");
            } else {
                projectService.assignUsersToProject(projectId, userId);
                redirectAttributes.addFlashAttribute("message", "User successfully assigned to the project.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Something went wrong. User was not assigned.");
        }

        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }


    @PostMapping("/delete/{projectToDeleteId}")
    public String deleteProject(@PathVariable Long projectToDeleteId, HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/projects/display";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to delete projects.");
            return "redirect:/projects/display";
        }

        try {
            Project projectToDelete = projectService.findProjectById(projectToDeleteId);
            if (projectToDelete == null) {
                redirectAttributes.addFlashAttribute("message", "Project not found.");
            } else {
                projectService.deleteProjectById(projectToDeleteId);
                redirectAttributes.addFlashAttribute("message", projectToDelete.getProjectName() + " was successfully deleted.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Something went wrong. The project was not deleted.");
        }

        return "redirect:/projects/display";
    }


    @PostMapping("/removeUser")
    public String removeUserFromProject(@RequestParam Long userId,
                                        @RequestParam Long projectId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/projects/display";
        }

        if (projectId == null || userId == null) {
            redirectAttributes.addFlashAttribute("message", "User or project information is missing. Please try again.");
            return "redirect:/projects/display";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "You are not authorized to remove assigned users.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        try {
            projectService.removeUserFromProject(userId, projectId);
            redirectAttributes.addFlashAttribute("message", "User was successfully removed from the project.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to remove user from the project due to an unexpected error.");
        }

        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }


    @GetMapping("/editFormDisplay/{projectId}")
    public String projectEditFormDisplay(@PathVariable Long projectId,
                                         Model model,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit this project.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        Project projectToEdit = projectService.findProjectById(projectId);
        if (projectToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Project to edit was not found.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        model.addAttribute("project", projectToEdit);
        return "/project/editForm";
    }


    @PostMapping("/editForm/{projectId}")
    public String editProject(@PathVariable Long projectId,
                              @ModelAttribute("project") Project projectDetails,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit this project.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        Project existingProject = projectService.findProjectById(projectId);
        if (existingProject == null) {
            redirectAttributes.addFlashAttribute("message", "Project to edit was not found.");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        projectService.editProject(projectId, projectDetails);
        redirectAttributes.addFlashAttribute("message", "Project updated successfully.");
        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }


}
