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

/*
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

    // Test: projectsAddFormDisplay
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
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);
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
            verify(redirectAttributes).addFlashAttribute("message", "User not authorized to add new projects");
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
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            Project project = new Project();
            project.setProjectName("Test Project");

            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(projectService).createProject(project);
            verify(redirectAttributes).addFlashAttribute("message", "You have successfully created a new project: " + project.getProjectName());
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

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            Project project = new Project();
            project.setProjectName("Test Project");

            doThrow(new RuntimeException("Service error")).when(projectService).createProject(project);

            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(redirectAttributes).addFlashAttribute("message", "User not authorized to create a new project.");
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
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            List<Project> projectList = new ArrayList<>();
            projectList.add(new Project());

            when(projectService.findAllProjects()).thenReturn(projectList);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
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

    // Test: assignUserToProject
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

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(false);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(projectService).assignUsersToProject(projectId, userId);
            verify(redirectAttributes).addFlashAttribute("message", "User successfully assigned to the project.");
        }
    }

    @Test
    public void testAssignUserToProject_UserAlreadyAssigned() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(true);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "User is already assigned to the project.");
        }
    }

    @Test
    public void testAssignUserToProject_Exception() {
        try (var mockedAppUtility = mockStatic(AppUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedAppUtility.when(() -> AppUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenThrow(new RuntimeException("Service error"));

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/overviewDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "Something went wrong. User was not assigned.");
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
 */
