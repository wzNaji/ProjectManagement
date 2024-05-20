package com.boefcity.projectmanagement.controller;


import com.boefcity.projectmanagement.config.AppUtility;
import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.service.ProjectService;
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
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;
    @Mock
    private UserService userService;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProjectController projectController;

    @Test
    public void testProjectsAddFormDisplay_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testProjectsAddFormDisplay_AdminRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            // Mock session, redirectAttributes, and model
            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
            Model model = mock(Model.class);

            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            // Mock static method calls
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(adminUser)).thenReturn(true);

            // Mock session and service method interactions
            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            // Invoke controller method
            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);

            // Verify the result and interactions
            assertEquals("/project/projectAddForm", result);
            verify(model).addAttribute(eq("project"), any(Project.class));
        }
    }


    @Test
    public void testProjectsAddFormDisplay_WorkerRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long userId = 1L;
            User workerUser = new User();
            workerUser.setUserId(userId);
            workerUser.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(workerUser);

            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(redirectAttributes).addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan benytte denne funktion");
        }
    }

    // Test: createProject
    @Test
    public void testCreateProject_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            Project project = new Project();
            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testCreateProject_AdminRole_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            // Mock session and redirectAttributes
            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            // Mock static method calls
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(adminUser)).thenReturn(true);

            // Mock session and service method interactions
            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            Project project = new Project();
            project.setProjectName("Test Project");

            // Invoke controller method
            String result = projectController.createProject(project, session, redirectAttributes);

            // Verify the result and interactions
            assertEquals("redirect:/projects/display", result);
            verify(projectService).createProject(project);
            verify(redirectAttributes).addFlashAttribute("message", "Projektet blev oprettet");
        }
    }


    @Test
    public void testCreateProject_AdminRole_Exception() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            Project project = new Project();
            project.setProjectName("Test Project");

            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(redirectAttributes).addFlashAttribute("message", "Kun ADMIN og MANAGER brugere kan benytte denne funktion");
        }
    }


    @Test
    public void testCreateProject_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long userId = 1L;
            User unauthorizedUser = new User();
            unauthorizedUser.setUserId(userId);
            unauthorizedUser.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(unauthorizedUser);

            Project project = new Project();
            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
        }
    }

    // Test: projectsDisplay
    @Test
    public void testProjectsDisplay_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testProjectsDisplay_AdminOrManagerRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            // Mock session, redirectAttributes, and model
            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
            Model model = mock(Model.class);

            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            // Mock static method calls
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(adminUser)).thenReturn(true);

            // Mock session and service method interactions
            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            List<Project> projectList = new ArrayList<>();
            projectList.add(new Project());

            when(projectService.findAllProjects()).thenReturn(projectList);

            // Invoke controller method
            String result = projectController.projectsDisplay(session, model, redirectAttributes);

            // Verify the result and interactions
            assertEquals("/project/projectList", result);
            verify(model).addAttribute("projectList", projectList);
        }
    }


    @Test
    public void testProjectsDisplay_WorkerRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long userId = 1L;
            User workerUser = new User();
            workerUser.setUserId(userId);
            workerUser.setUserRole(Role.WORKER);

            List<Project> projectList = new ArrayList<>();
            workerUser.setProjects(projectList);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(workerUser);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("/project/projectList", result);
            verify(model).addAttribute("projectList", projectList);
        }
    }

    // Test: assignUserDisplay
    @Test
    public void testAssignUserDisplay_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.projectsOverviewDisplay(session, model, 1L, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testAssignUserToProject_NotAuthenticated() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.assignUserToProject(1L, 2L, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testAssignUserToProject_AdminRole_Success() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);


            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(currentUser)).thenReturn(true);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(false);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);

            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(projectService).assignUsersToProject(projectId, userId);
            verify(redirectAttributes).addFlashAttribute("message", "Brugeren blev tilføjet til projektet");
        }
    }


    @Test
    public void testAssignUserToProject_UserAlreadyAssigned() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {

            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(currentUser)).thenReturn(true);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(true);


            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);

            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "Brugeren er allerede tilføjet til dette projekt");
        }
    }


    @Test
    public void testAssignUserToProject_Exception() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            // Mock session and redirectAttributes
            HttpSession session = mock(HttpSession.class);
            RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            // Mock static method calls
            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);
            mockedAppUtility.when(() -> AppUtility.isAdminOrManager(currentUser)).thenReturn(true);

            // Mock session and service method interactions
            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenThrow(new RuntimeException("Service error"));

            // Invoke controller method
            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);

            // Verify the result and interactions
            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "Noget gik galt. Prøv venligst igen");
        }
    }


    @Test
    public void testAssignUserToProject_UnauthorizedRole() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.WORKER);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=1", result);
        }
    }

}
