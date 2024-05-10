package com.boefcity.projectmanagement.controller;

import com.boefcity.projectmanagement.config.SessionUtility;
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

    // Test: projectsAddFormDisplay
    @Test
    public void testProjectsAddFormDisplay_NotAuthenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testProjectsAddFormDisplay_AdminRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            String result = projectController.projectsAddFormDisplay(model, session, redirectAttributes);
            assertEquals("/project/projectAddForm", result);
            verify(model).addAttribute(eq("project"), any(Project.class));
        }
    }

    @Test
    public void testProjectsAddFormDisplay_WorkerRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User workerUser = new User();
            workerUser.setUserId(userId);
            workerUser.setUserRole(Role.WORKER);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

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
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            Project project = new Project();
            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testCreateProject_AdminRole_Success() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

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
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            Project project = new Project();
            project.setProjectName("Test Project");

            doThrow(new RuntimeException("Service error")).when(projectService).createProject(project);

            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/projects/display", result);
            verify(redirectAttributes).addFlashAttribute("message", "Something went wrong. Please try again.");
        }
    }

    @Test
    public void testCreateProject_UnauthorizedRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User unauthorizedUser = new User();
            unauthorizedUser.setUserId(userId);
            unauthorizedUser.setUserRole(Role.WORKER);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(unauthorizedUser);

            Project project = new Project();
            String result = projectController.createProject(project, session, redirectAttributes);
            assertEquals("redirect:/errorPage", result);
        }
    }

    // Test: projectsDisplay
    @Test
    public void testProjectsDisplay_NotAuthenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testProjectsDisplay_AdminRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);

            List<Project> adminProjectList = new ArrayList<>();
            adminProjectList.add(new Project());

            when(projectService.findAll()).thenReturn(adminProjectList);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("/project/adminProjectList", result);
            verify(model).addAttribute("adminProjectList", adminProjectList);
        }
    }

    @Test
    public void testProjectsDisplay_WorkerRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User workerUser = new User();
            workerUser.setUserId(userId);
            workerUser.setUserRole(Role.WORKER);

            List<Project> userProjects = new ArrayList<>();
            workerUser.setProjects(userProjects);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(workerUser);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("/project/userProjectList", result);
            verify(model).addAttribute("userProjectList", userProjects);
        }
    }

    @Test
    public void testProjectsDisplay_ManagerRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            User managerUser = new User();
            managerUser.setUserId(userId);
            managerUser.setUserRole(Role.WORKER);

            List<Project> userProjects = new ArrayList<>();
            managerUser.setProjects(userProjects);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(managerUser);

            String result = projectController.projectsDisplay(session, model, redirectAttributes);
            assertEquals("/project/userProjectList", result);
            verify(model).addAttribute("userProjectList", userProjects);
        }
    }

    // Test: assignUserDisplay
    @Test
    public void testAssignUserDisplay_NotAuthenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.assignUserDisplay(session, model, 1L, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testAssignUserDisplay_AdminRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            Long projectId = 1L;
            User adminUser = new User();
            adminUser.setUserId(userId);
            adminUser.setUserRole(Role.ADMIN);

            Project project = new Project();
            List<User> allUsers = new ArrayList<>();
            List<User> assignedUsers = new ArrayList<>();

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(adminUser);
            when(projectService.findById(projectId)).thenReturn(project);
            when(userService.findAllUsers()).thenReturn(allUsers);

            String result = projectController.assignUserDisplay(session, model, projectId, redirectAttributes);
            assertEquals("project/assignUser", result);
            verify(model).addAttribute("project", project);
            verify(model).addAttribute("allUsers", allUsers);
            verify(model).addAttribute("assignedUsers", assignedUsers);
        }
    }

    @Test
    public void testAssignUserDisplay_ErrorPage() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long userId = 1L;
            Long projectId = 1L;
            User unauthorizedUser = new User();
            unauthorizedUser.setUserId(userId);
            unauthorizedUser.setUserRole(Role.WORKER);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userService.findUserById(userId)).thenReturn(unauthorizedUser);

            String result = projectController.assignUserDisplay(session, model, projectId, redirectAttributes);
            assertEquals("errorPage", result);
        }
    }

    // Test: assignUserToProject
    @Test
    public void testAssignUserToProject_NotAuthenticated() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(true);

            String result = projectController.assignUserToProject(1L, 2L, session, redirectAttributes);
            assertEquals("redirect:/users/loginDisplay", result);
        }
    }

    @Test
    public void testAssignUserToProject_AdminRole_Success() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(false);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/assignUserDisplay?projectId=" + projectId, result);
            verify(projectService).assignUsersToProject(projectId, userId);
            verify(redirectAttributes).addFlashAttribute("message", "User successfully assigned to the project.");
        }
    }

    @Test
    public void testAssignUserToProject_UserAlreadyAssigned() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenReturn(true);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/assignUserDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "User is already assigned to the project.");
        }
    }

    @Test
    public void testAssignUserToProject_Exception() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.ADMIN);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);
            when(projectService.isUserAssignedToProject(projectId, userId)).thenThrow(new RuntimeException("Service error"));

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/projects/assignUserDisplay?projectId=" + projectId, result);
            verify(redirectAttributes).addFlashAttribute("message", "Something went wrong. User was not assigned.");
        }
    }

    @Test
    public void testAssignUserToProject_UnauthorizedRole() {
        try (var mockedSessionUtility = mockStatic(SessionUtility.class)) {
            Long projectId = 1L;
            Long userId = 2L;
            Long currentUserId = 3L;
            User currentUser = new User();
            currentUser.setUserId(currentUserId);
            currentUser.setUserRole(Role.WORKER);

            mockedSessionUtility.when(() -> SessionUtility.isNotAuthenticated(session, redirectAttributes)).thenReturn(false);

            when(session.getAttribute("userId")).thenReturn(currentUserId);
            when(userService.findUserById(currentUserId)).thenReturn(currentUser);

            String result = projectController.assignUserToProject(projectId, userId, session, redirectAttributes);
            assertEquals("redirect:/errorPage", result);
        }
    }
}
