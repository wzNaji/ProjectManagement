package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.PriorityLevel;
import com.boefcity.projectmanagement.model.Status;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.Task;
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
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubprojectRepository subprojectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private Subproject subproject;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTaskId(1L);
        task.setTaskName("Test Task");

        subproject = new Subproject();
        subproject.setSubprojectId(1L);
        subproject.setTasks(new ArrayList<>(List.of(task)));
    }

    @Test
    void testCreateTask() {
        taskService.createTask(task);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testCreateTask_NullTask() {
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(null));
    }

    @Test
    void testFindByTaskId() {
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(task);

        Task foundTask = taskService.findByTaskId(1L);

        assertEquals(task, foundTask);
    }

    @Test
    void testFindByTaskId_NotFound() {
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.findByTaskId(1L));
    }

    @Test
    void testEditTask() {
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(task);

        Task updatedTask = new Task();
        updatedTask.setTaskName("Updated Task");
        updatedTask.setTaskDescription("Updated Description");
        updatedTask.setPriorityLevel(PriorityLevel.HIGH);
        updatedTask.setStatus(Status.ACTIVE);

        taskService.editTask(1L, updatedTask);

        verify(taskRepository, times(1)).save(task);
        assertEquals("Updated Task", task.getTaskName());
        assertEquals("Updated Description", task.getTaskDescription());
        assertEquals(PriorityLevel.HIGH, task.getPriorityLevel());
        assertEquals(Status.ACTIVE, task.getStatus());
    }

    @Test
    void testEditTask_NotFound() {
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(null);

        Task updatedTask = new Task();

        assertThrows(EntityNotFoundException.class, () -> taskService.editTask(1L, updatedTask));
    }

    @Test
    void testDeleteTask() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(task);

        taskService.deleteTask(1L, 1L);

        verify(taskRepository, times(1)).delete(task);
        verify(subprojectRepository, times(1)).save(subproject);
    }

    @Test
    void testDeleteTask_SubprojectNotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(1L, 1L));
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        when(subprojectRepository.findSubprojectByIdNative(1L)).thenReturn(subproject);
        when(taskRepository.findTaskByIdNative(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(1L, 1L));
    }
}
