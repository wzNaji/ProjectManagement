package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubprojectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/subprojects")
public class SubprojectController {
    private final SubprojectService subprojectService;
    private final UserService userService;
    private final ProjectService projectService;

    public SubprojectController(SubprojectService subprojectService, UserService userService, ProjectService projectService) {
        this.subprojectService = subprojectService;
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
            model.addAttribute("subproject", new Subproject());
            model.addAttribute("project", projectToFind);
            model.addAttribute("priorityLevel", PriorityLevel.values());
            model.addAttribute("status", Status.values());
            return "/project/subproject/subprojectAddForm";
        } else if (Role.WORKER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to add new sub project");
            return "redirect:/projects/display";
        }
        return "/errorPage";
    }

    @PostMapping("/addForm")
    public String createAndAssignSubproject(@RequestParam("projectId") Long projectId,
                                      @ModelAttribute Subproject subproject,
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
                subprojectService.createSubproject(subproject);
                projectService.assignSubprojectToProject(subproject, projectId);

                redirectAttributes.addFlashAttribute("message",
                        "You have successfully created a new subproject: " + subproject.getSubprojectName());
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
    public String deleteSubproject(@RequestParam("subprojectId") Long subprojectId,
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
            redirectAttributes.addFlashAttribute("message", "You are not authorized to delete subprojects");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        try {
            Subproject subprojectToDelete = subprojectService.findBySubprojectId(subprojectId);
            if (subprojectToDelete != null) {
                subprojectService.deleteSubproject(subprojectId, projectId);
                redirectAttributes.addFlashAttribute("message",
                        "You have successfully deleted sub project: " + subprojectToDelete.getSubprojectName());
            } else {
                redirectAttributes.addFlashAttribute("message", "Subproject not found.");
            }
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting subproject");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }
    }

    // edit subproject

    @GetMapping("/editFormDisplay/{subprojectId}")
    public String editSubproject(@PathVariable Long subprojectId,
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
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit subprojects");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Subproject subprojectToEdit = subprojectService.findBySubprojectId(subprojectId);
        if (subprojectToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Subproject to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("subproject", subprojectToEdit);
        model.addAttribute("projectId", projectId);
        return "/project/subproject/subprojectEditForm";
    }

    @PostMapping("/editForm/{subprojectId}")
    public String editSubproject(@PathVariable Long subprojectId,
                             @RequestParam Long projectId,
                             @ModelAttribute("subproject") Subproject subprojectDetails,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (SessionUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User currentUser = userService.findUserById(currentUserId);
        Role role = currentUser.getUserRole();

        if (!Role.ADMIN.equals(role) && !Role.MANAGER.equals(role)) {
            redirectAttributes.addFlashAttribute("message", "User not authorized to edit subprojects");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }

        Subproject existingSubproject = subprojectService.findBySubprojectId(subprojectId);
        if (existingSubproject == null) {
            redirectAttributes.addFlashAttribute("message", "Subproject to edit was not found");
            return "redirect:/projects/editDisplay?projectId=" + projectId;
        }


        subprojectService.updateSubproject(subprojectId, subprojectDetails);

        redirectAttributes.addFlashAttribute("message", "Subproject updated successfully");
        return "redirect:/projects/editDisplay?projectId=" + projectId;
    }


}

