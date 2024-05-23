package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class SubprojectServiceImplTest {

    @Mock
    private SubprojectRepository subprojectRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubprojectServiceImpl subprojectService;

    private Subproject subproject;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        subproject = new Subproject();
        subproject.setSubprojectId(1L);
        subproject.setSubprojectCost(100.0);
        subproject.setSubprojectHours(10.0);

        project = new Project();
        project.setProjectId(1L);
        project.setProjectCost(500.0);
        project.setProjectActualdHours(50.0);
        project.setSubprojects(new ArrayList<>(List.of(subproject)));

        task = new Task();
        task.setTaskId(1L);
    }

    @Test
    void testCreateSubproject() {
        subprojectService.createSubproject(subproject);
        verify(subprojectRepository, times(1)).save(subproject);
    }

    @Test
    void testCreateSubproject_NullSubproject() {
        assertThrows(IllegalArgumentException.class, () -> subprojectService.createSubproject(null));
    }

    @Test
    void testDeleteSubproject() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);

        subprojectService.deleteSubproject(1L, 1L);

        verify(projectRepository, times(1)).save(project);
        verify(subprojectRepository, times(1)).delete(subproject);
    }

    @Test
    void testDeleteSubproject_ProjectNotFound() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> subprojectService.deleteSubproject(1L, 1L));
    }

    @Test
    void testDeleteSubproject_SubprojectNotFound() {
        when(projectRepository.findProjectByIdNative(1L)).thenReturn(project);
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> subprojectService.deleteSubproject(1L, 1L));
    }

    @Test
    void testAssignTaskToSubproject() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(task);

        subprojectService.assignTaskToSubproject(task, 1L);

        verify(subprojectRepository, times(1)).save(subproject);
    }

    @Test
    void testAssignTaskToSubproject_SubprojectNotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> subprojectService.assignTaskToSubproject(task, 1L));
    }

    @Test
    void testAssignTaskToSubproject_TaskNotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> subprojectService.assignTaskToSubproject(task, 1L));
    }

    @Test
    void testFindBySubprojectId() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);

        Subproject foundSubproject = subprojectService.findBySubprojectId(1L);

        assertEquals(subproject, foundSubproject);
    }

    @Test
    void testFindBySubprojectId_NotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> subprojectService.findBySubprojectId(1L));
    }

    @Test
    void testUpdateSubproject() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        when(projectRepository.save(project)).thenReturn(project);

        subproject.setProject(project);
        subprojectService.updateSubproject(1L, subproject);

        verify(subprojectRepository, times(1)).save(subproject);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testUpdateSubproject_NotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> subprojectService.updateSubproject(1L, subproject));
    }
}
