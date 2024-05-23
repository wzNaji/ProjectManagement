package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubprojectRepository subprojectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private User user;
    private Subproject subproject;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Test Project");

        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        subproject = new Subproject();
        subproject.setSubprojectId(1L);
        subproject.setSubprojectName("Test Subproject");
    }

    @Test
    void testCreateProject() {
        projectService.createProject(project);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testFindProjectById() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        Project foundProject = projectService.findProjectById(1L);
        assertNotNull(foundProject);
        assertEquals("Test Project", foundProject.getProjectName());
    }

    @Test
    void testDeleteProjectById() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        projectService.deleteProjectById(1L);
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void testFindAllProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(projectRepository.findAll()).thenReturn(projects);
        List<Project> foundProjects = projectService.findAllProjects();
        assertNotNull(foundProjects);
        assertFalse(foundProjects.isEmpty());
    }

    @Test
    void testAssignUsersToProject() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        when(userRepository.findUserByIdNative(1L)).thenReturn(user);
        projectService.assignUsersToProject(1L, 1L);
        verify(userRepository, times(1)).save(user);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testIsUserAssignedToProject() {
        when(projectRepository.existsByProjectIdAndUsersUserId(1L, 1L)).thenReturn(true);
        boolean isAssigned = projectService.isUserAssignedToProject(1L, 1L);
        assertTrue(isAssigned);
    }

    @Test
    void testAssignSubprojectToProject() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        projectService.assignSubprojectToProject(subproject, 1L);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testRemoveUserFromProject() {
        when(userRepository.findUserByIdNative(1L)).thenReturn(user);
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        projectService.removeUserFromProject(1L, 1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEditProject() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        Project updatedProject = new Project();
        updatedProject.setProjectName("Updated Project");
        projectService.editProject(1L, updatedProject);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    // edge cases
    @Test
    void testCreateProject_NullProject() {
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(null));
    }

    @Test
    void testFindProjectById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> projectService.findProjectById(null));
    }


    @Test
    void testAssignSubprojectToProject_NullProjectId() {
        assertThrows(IllegalArgumentException.class, () -> projectService.assignSubprojectToProject(subproject, null));
    }

    @Test
    void testRemoveUserFromProject_NullUserId() {
        assertThrows(IllegalArgumentException.class, () -> projectService.removeUserFromProject(null, 1L));
    }

    @Test
    void testRemoveUserFromProject_NullProjectId() {
        assertThrows(IllegalArgumentException.class, () -> projectService.removeUserFromProject(1L, null));
    }


}
