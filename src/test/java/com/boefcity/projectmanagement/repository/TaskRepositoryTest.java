package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testFindTaskByIdNative() {
        Task task = new Task();
        task.setTaskName("Test Task");

        task = taskRepository.save(task);

        Task foundTask = taskRepository.findTaskByIdNative(task.getTaskId());
        assertNotNull(foundTask);
        assertEquals(task.getTaskName(), foundTask.getTaskName());
    }
}
