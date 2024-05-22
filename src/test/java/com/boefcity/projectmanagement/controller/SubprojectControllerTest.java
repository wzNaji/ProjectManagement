package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.*;
import com.boefcity.projectmanagement.service.ProjectService;
import com.boefcity.projectmanagement.service.SubprojectService;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class SubprojectControllerTest {

    @Mock
    private SubprojectService subprojectService;
    @Mock
    private UserService userService;
    @Mock
    private ProjectService projectService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @InjectMocks
    private SubprojectController subprojectController;

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testSubprojectAddFormDisplay_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.subprojectAddFormDisplay(1L, model, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret, hvis rollen ikke er ADMIN eller MANAGER
    @Test
    public void testSubprojectAddFormDisplay_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);
            when(projectService.findProjectById(1L)).thenReturn(new Project());

            String result = subprojectController.subprojectAddFormDisplay(1L, model, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(redirectAttributes).addFlashAttribute("message", "Denne side kan kun vises til ADMIN og MANAGER brugere");
        }
    }

    // Test for at sikre, at subprojektet kan tilføjes, hvis brugeren er ADMIN eller MANAGER
    @Test
    public void testSubprojectAddFormDisplay_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Project project = new Project();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);
            when(projectService.findProjectById(1L)).thenReturn(project);

            String result = subprojectController.subprojectAddFormDisplay(1L, model, session, redirectAttributes);
            assertEquals("/project/subproject/subprojectAddForm", result);
            verify(model).addAttribute("subproject", new Subproject());
            verify(model).addAttribute("project", project);
            verify(model).addAttribute("priorityLevel", PriorityLevel.values());
            verify(model).addAttribute("status", Status.values());
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testCreateAndAssignSubproject_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.createAndAssignSubproject(1L, new Subproject(), session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret, hvis rollen ikke er ADMIN eller MANAGER
    @Test
    public void testCreateAndAssignSubproject_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(false);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            String result = subprojectController.createAndAssignSubproject(1L, new Subproject(), session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan tilgå denne funktion");
        }
    }

    // Test for at sikre, at subprojektet kan oprettes og tildeles, hvis brugeren er ADMIN eller MANAGER
    @Test
    public void testCreateAndAssignSubproject_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Subproject subproject = new Subproject();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            doNothing().when(subprojectService).createSubproject(subproject);
            doNothing().when(projectService).assignSubprojectToProject(subproject, 1L);

            String result = subprojectController.createAndAssignSubproject(1L, subproject, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Subprojektet er oprettet");
        }
    }

    // Test for at håndtere fejl under oprettelsen af subprojekt
    @Test
    public void testCreateAndAssignSubproject_Failure() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Subproject subproject = new Subproject();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            doThrow(new RuntimeException()).when(subprojectService).createSubproject(subproject);

            String result = subprojectController.createAndAssignSubproject(1L, subproject, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Noget galt. Prøv venligst igen.");
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testDeleteSubproject_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.deleteSubproject(1L, 1L, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret, hvis rollen ikke er ADMIN eller MANAGER
    @Test
    public void testDeleteSubproject_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(false);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            String result = subprojectController.deleteSubproject(1L, 1L, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan slette subprojekter");
        }
    }

    // Test for at sikre, at subprojektet kan slettes, hvis brugeren er ADMIN eller MANAGER
    @Test
    public void testDeleteSubproject_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Subproject subproject = new Subproject();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);
            when(subprojectService.findBySubprojectId(1L)).thenReturn(subproject);

            doNothing().when(subprojectService).deleteSubproject(1L, 1L);

            String result = subprojectController.deleteSubproject(1L, 1L, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Subprojektet blev slettet");
        }
    }

    // Test for at håndtere fejl under sletningen af subprojekt
    @Test
    public void testDeleteSubproject_Failure() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);

            Subproject subproject = new Subproject(); // Mock Subproject for at undgå "Subprojektet blev ikke fundet" besked

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);
            when(subprojectService.findBySubprojectId(1L)).thenReturn(subproject); // Mock findBySubprojectId for at returnere et Subproject

            doThrow(new RuntimeException()).when(subprojectService).deleteSubproject(1L, 1L);

            String result = subprojectController.deleteSubproject(1L, 1L, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Fejl under sletning af subprojektet");
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testEditSubproject_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.editSubproject(1L, 1L, model, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at håndtere tilfælde, hvor subprojektet ikke blev fundet
    @Test
    public void testEditSubproject_SubprojectNotFound() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(subprojectService.findBySubprojectId(1L)).thenReturn(null);

            String result = subprojectController.editSubproject(1L, 1L, model, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Subprojektet til redigering blev ikke fundet");
        }
    }

    // Test for at sikre, at subprojektet kan redigeres, hvis brugeren er ADMIN eller MANAGER
    @Test
    public void testEditSubproject_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            Subproject subproject = new Subproject();
            when(subprojectService.findBySubprojectId(1L)).thenReturn(subproject);

            String result = subprojectController.editSubproject(1L, 1L, model, session, redirectAttributes);
            assertEquals("/project/subproject/subprojectEditForm", result);
            verify(model).addAttribute("priorityLevel", PriorityLevel.values());
            verify(model).addAttribute("status", Status.values());
            verify(model).addAttribute("subproject", subproject);
            verify(model).addAttribute("projectId", 1L);
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testEditSubprojectFormSubmission_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.editSubproject(1L, 1L, new Subproject(), session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret, hvis rollen ikke er ADMIN eller MANAGER
    @Test
    public void testEditSubprojectFormSubmission_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(false);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            String result = subprojectController.editSubproject(1L, 1L, new Subproject(), session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Kun ADMIN og MANAGER brugere har tilladelse til denne function");
        }
    }

    // Test for at sikre, at subprojektet kan opdateres, hvis brugeren er ADMIN eller MANAGER
    @Test
    public void testEditSubprojectFormSubmission_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Subproject subprojectDetails = new Subproject();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            Subproject existingSubproject = new Subproject();
            when(subprojectService.findBySubprojectId(1L)).thenReturn(existingSubproject);
            doNothing().when(subprojectService).updateSubproject(1L, subprojectDetails);

            String result = subprojectController.editSubproject(1L, 1L, subprojectDetails, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Subprojektet blev opdateret");
        }
    }

    // Test for at håndtere fejl under opdateringen af subprojekt
    @Test
    public void testEditSubprojectFormSubmission_Failure() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            User user = new User();
            user.setUserRole(Role.MANAGER);
            Subproject subprojectDetails = new Subproject();

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(user)).thenReturn(true);
            when(session.getAttribute("userId")).thenReturn(1L);
            when(userService.findUserById(1L)).thenReturn(user);

            Subproject existingSubproject = new Subproject();
            when(subprojectService.findBySubprojectId(1L)).thenReturn(existingSubproject);
            doThrow(new RuntimeException()).when(subprojectService).updateSubproject(1L, subprojectDetails);

            String result = subprojectController.editSubproject(1L, 1L, subprojectDetails, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Noget gik galt. Prøv venligst igen.");
        }
    }

    // Test for at sikre, at brugeren bliver omdirigeret til login-siden, hvis ikke autentificeret
    @Test
    public void testSubprojectsOverviewDisplay_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = subprojectController.subprojectsOverviewDisplay(session, model, 1L, 1L, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    // Test for at håndtere tilfælde, hvor subprojektet ikke blev fundet
    @Test
    public void testSubprojectsOverviewDisplay_SubprojectNotFound() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(projectService.findProjectById(1L)).thenReturn(new Project());
            when(subprojectService.findBySubprojectId(1L)).thenReturn(null);

            String result = subprojectController.subprojectsOverviewDisplay(session, model, 1L, 1L, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Subprojekt blev ikke fundet.");
        }
    }

    // Test for at håndtere tilfælde, hvor projektet ikke blev fundet
    @Test
    public void testSubprojectsOverviewDisplay_ProjectNotFound() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(projectService.findProjectById(1L)).thenReturn(null);
            when(subprojectService.findBySubprojectId(1L)).thenReturn(new Subproject());

            String result = subprojectController.subprojectsOverviewDisplay(session, model, 1L, 1L, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
            verify(redirectAttributes).addFlashAttribute("message", "Projektet blev ikke fundet.");
        }
    }

    // Test for at sikre korrekt visning af subprojekts oversigt
    @Test
    public void testSubprojectsOverviewDisplay_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Project project = new Project();
            Subproject subproject = new Subproject();
            List<Task> tasks = new ArrayList<>();

            subproject.setTasks(tasks);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            when(projectService.findProjectById(1L)).thenReturn(project);
            when(subprojectService.findBySubprojectId(1L)).thenReturn(subproject);

            String result = subprojectController.subprojectsOverviewDisplay(session, model, 1L, 1L, redirectAttributes);
            assertEquals("project/subproject/subprojectOverviewDisplay", result);
            verify(model).addAttribute("project", project);
            verify(model).addAttribute("subproject", subproject);
            verify(model).addAttribute("subprojectTasks", tasks);
        }
    }
}
