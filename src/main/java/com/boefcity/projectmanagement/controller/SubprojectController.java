package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubprojectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


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
    public String subprojectAddFormDisplay(@RequestParam("projectId") Long projectId, Model model, HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);
        Project projectToFind = projectService.findProjectById(projectId);

        if (!AppUtility.isAdminOrManager(user)) {
                redirectAttributes.addFlashAttribute("message", "Denne side kan kun vises til ADMIN og MANAGER brugere");
                return "redirect:/projects/display";
            }
        model.addAttribute("subproject", new Subproject());
        model.addAttribute("project", projectToFind);
        model.addAttribute("priorityLevel", PriorityLevel.values());
        model.addAttribute("status", Status.values());

        return "/project/subproject/subprojectAddForm";
    }

    @PostMapping("/addForm")
    public String createAndAssignSubproject(@RequestParam("projectId") Long projectId,
                                            @ModelAttribute Subproject subproject,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        if (!AppUtility.isAdminOrManager(user)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan tilgå denne funktion");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        try {
            subprojectService.createSubproject(subproject);
            projectService.assignSubprojectToProject(subproject, projectId);

            redirectAttributes.addFlashAttribute("message", "Subprojektet er oprettet");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget galt. Prøv venligst igen.");
        }

        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }


    // Delete sub project

    @PostMapping("/delete")
    public String deleteSubproject(@RequestParam("subprojectId") Long subprojectId,
                                   @RequestParam("projectId") Long projectId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        if (!AppUtility.isAdminOrManager(user)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan slette subprojekter");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        try {
            Subproject subprojectToDelete = subprojectService.findBySubprojectId(subprojectId);

            if (subprojectToDelete == null) {
                redirectAttributes.addFlashAttribute("message", "Subprojektet blev ikke fundet");
            } else {
                subprojectService.deleteSubproject(subprojectId, projectId);
                redirectAttributes.addFlashAttribute("message", "Subprojektet blev slettet");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Fejl under sletning af subprojektet");
        }

        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }


    // edit subproject

    @GetMapping("/editFormDisplay/{subprojectId}")
    public String editSubproject(@PathVariable Long subprojectId,
                           @RequestParam Long projectId,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Subproject subprojectToEdit = subprojectService.findBySubprojectId(subprojectId);
        if (subprojectToEdit == null) {
            redirectAttributes.addFlashAttribute("message", "Subprojektet til redigering blev ikke fundet");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
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

        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(currentUserId);

        if (!AppUtility.isAdminOrManager(user)) {
            redirectAttributes.addFlashAttribute("message", "Kun ADMIN og MANAGER brugere har tilladelse til denne function");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        Subproject existingSubproject = subprojectService.findBySubprojectId(subprojectId);
        if (existingSubproject == null) {
            redirectAttributes.addFlashAttribute("message", "Subprojektet til redigering blev ikke fundet");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        try {
            subprojectService.updateSubproject(subprojectId, subprojectDetails);
            redirectAttributes.addFlashAttribute("message", "Subprojektet blev opdateret succesfuldt");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Noget gik galt. Prøv venligst igen.");
        }

        redirectAttributes.addFlashAttribute("message", "Subproject updated successfully");
        return "redirect:/projects/overviewDisplay?projectId=" + projectId;
    }

    @GetMapping("/overviewDisplay")
    public String subprojectsOverviewDisplay(HttpSession session, Model model, @RequestParam Long subprojectId,
                                             @RequestParam Long projectId,
                                             RedirectAttributes redirectAttributes) {
        if (AppUtility.isNotAuthenticated(session, redirectAttributes)) {
            return "redirect:/users/loginDisplay";
        }
        Project project;
        Subproject subproject;
        try {
            project = projectService.findProjectById(projectId);
            subproject = subprojectService.findBySubprojectId(subprojectId);
            if (subproject == null) {
                redirectAttributes.addFlashAttribute("message", "Subprojekt blev ikke fundet.");
                return "redirect:/projects/overviewDisplay?projectId=" + projectId;
            }
            if (project == null) {
                redirectAttributes.addFlashAttribute("message", "Projektet blev ikke fundet.");
                return "redirect:/projects/overviewDisplay?projectId=" + projectId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Fejl ved hetning af subprojekt. Prøv venligst igen");
            return "redirect:/projects/overviewDisplay?projectId=" + projectId;
        }

        List<Task> subprojectTasks = subproject.getTasks(); // Kald fra service!

        model.addAttribute("project", project);
        model.addAttribute("subproject", subproject);
        model.addAttribute("subprojectTasks", subprojectTasks);

        return "project/subproject/subprojectOverviewDisplay";
    }


}

