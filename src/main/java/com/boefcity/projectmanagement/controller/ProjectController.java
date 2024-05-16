package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
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

    @GetMapping("/addForm")
    public String projectsAddFormDisplay(Model model, HttpSession session,
                                         RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            model.addAttribute("project", new Project());
            return "/project/projectAddForm";
        } else if (Role.WORKER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to add new projects");
            return "redirect:/projects/display";
        }
        return "redirect:/errorPage";
    }

    @PostMapping("/createProject")
    public String createProject(@ModelAttribute Project project,
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
                projectService.createProject(project);
                redirectAttributes.addFlashAttribute("message",
                        "You have successfully created a new project: " + project.getProjectName());
                return "redirect:/projects/display";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Something went wrong. Please try again.");
                return "redirect:/projects/display";
            }
        }

        return "redirect:/errorPage";
    }

    @GetMapping("/display")
    public String projectsDisplay(HttpSession session, Model model,
                                  RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        Role role = user.getUserRole();

        if (Role.ADMIN.equals(role)) {
            List<Project> adminProjectList = projectService.findAll();
            model.addAttribute("adminProjectList", adminProjectList);
            return "/project/adminProjectList";
        } else if (Role.WORKER.equals(role) || Role.MANAGER.equals(role)) {
            List<Project> projectList = user.getProjects();
            model.addAttribute("userProjectList", projectList);
            return "/project/userProjectList";
        }
        return "redirect:/errorPage";
    }

// OVERVIEW DISPLAY
    @GetMapping("/editDisplay")
    public String editProjectDisplay(HttpSession session, Model model, @RequestParam Long projectId,
                                    RedirectAttributes redirectAttributes) {
        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(userId);

        Role role = currentUser.getUserRole();

        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            Project project = projectService.findById(projectId);
            List<User> allUsers = userService.findAllUsers();
            List<User> assignedUsers = project.getUsers().stream().toList();

            Project projectToFind = projectService.findById(projectId);
            List<Subproject> projectSubprojects = projectToFind.getSubprojects();

            model.addAttribute("project", project);
            model.addAttribute("allUsers", allUsers);
            model.addAttribute("assignedUsers", assignedUsers);
            model.addAttribute("projectSubprojects", projectSubprojects);

            String message = (String) session.getAttribute("message");
            if (message != null) {
                model.addAttribute("message", message);
                session.removeAttribute("message");
            }

            return "project/editDisplay";
        }

        return "errorPage";
    }

    @PostMapping("/assignUser")
    public String assignUserToProject(@RequestParam Long projectId, @RequestParam Long userId,
                                      HttpSession session, RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        Role role = currentUser.getUserRole();
        if (Role.ADMIN.equals(role) || Role.MANAGER.equals(role)) {
            try {
                if (projectService.isUserAssignedToProject(projectId, userId)) {
                    redirectAttributes.addFlashAttribute("message", "User is already assigned to the project.");
                    return "redirect:/projects/editDisplay?projectId=" + projectId;
                }

                projectService.assignUsersToProject(projectId, userId);
                redirectAttributes.addFlashAttribute("message", "User successfully assigned to the project.");
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("message", "Something went wrong. User was not assigned.");
                return "redirect:/projects/editDisplay?projectId=" + projectId;
            }
        }
        return "redirect:/errorPage";
    }

    @PostMapping("/delete/{projectToDeleteId}")
    public String deleteProject(@PathVariable Long projectToDeleteId, HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/projects/display";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);

        Role role = currentUser.getUserRole();
        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            return "errorPage";
        }

        Project projectToDelete = projectService.findById(projectToDeleteId);
        if (projectToDelete != null) {
            projectService.deleteById(projectToDeleteId);
            redirectAttributes.addFlashAttribute("message", projectToDelete.getProjectName() + " was successfully deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Project not found.");
        }

        return "redirect:/projects/display";
    }

    @PostMapping("/removeUser")
    public String removeUserFromProject(@RequestParam Long userId,
                                        @RequestParam Long projectId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/projects/display";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();
        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "You are not authorized to remove assigned users");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        if (projectId == null || userId == null) {
            redirectAttributes.addFlashAttribute("message", "User or project information is missing. Please try again.");
            return "redirect:/projects/display";
        }

        try {
            projectService.removeUserFromProject(userId, projectId);
            redirectAttributes.addFlashAttribute("message", "User was successfully removed from the project.");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to remove user from the project due to unexpected error.");
            return "redirect:/errorPage";
        }
    }


    // Skal have en request param til ORGANIZATION
    @GetMapping("/editFormDisplay/{projectId}")
    public String projectEditFormDisplay(@PathVariable Long projectId,
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
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit this project");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Project projectToEdit = projectService.findById(projectId);
        if (projectToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Project to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        model.addAttribute("project", projectToEdit);
        return "/project/editForm";
    }

    @PostMapping("/editForm/{projectId}")
    public String updateProject(@PathVariable Long projectId,
                                @ModelAttribute("project") Project projectDetails,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit this project");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Project existingProject = projectService.findById(projectId);
        if (existingProject == null) {
            redirectAttributes.addFlashAttribute("message", "Project to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }


        projectService.updateProject(projectId, projectDetails);

        redirectAttributes.addFlashAttribute("message", "Project updated successfully");
        return "redirect:/projects/editDisplay?projectId=" + projectId;
    }



}



