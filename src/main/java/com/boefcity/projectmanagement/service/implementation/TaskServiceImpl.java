package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.repository.TaskRepository;
import com.boefcity.projectmanagement.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubprojectRepository subprojectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, SubprojectRepository subprojectRepository) {
        this.taskRepository = taskRepository;
        this.subprojectRepository = subprojectRepository;
    }

    @Override
    public void createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task blev ikke fundet");
        }
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Task blev ikke gemt");
        }
    }

    @Override
    public Task findByTaskId(Long taskId) {
        Task task = taskRepository.findTaskByIdNative(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task kunne ikke findes");
        }
        return task;
    }

    @Override
    public void editTask(Long taskId, Task taskDetails) {
        Task taskToEdit = taskRepository.findTaskByIdNative(taskId);

        if (taskToEdit == null) {
            throw new EntityNotFoundException("Task kunne ikke findes");
        }
        //Setter det eksisterende subprojekts attributer til nye værdier, så vi efterfølgende kan opdatere og gemme.
        taskToEdit.setTaskName(taskDetails.getTaskName());
        taskToEdit.setTaskDescription(taskDetails.getTaskDescription());
        taskToEdit.setPriorityLevel(taskDetails.getPriorityLevel());
        taskToEdit.setStatus(taskDetails.getStatus());

        taskRepository.save(taskToEdit);
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId, Long subprojectId) {
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);
        Task task = taskRepository.findTaskByIdNative(taskId);
        if (subproject == null) {
            throw new IllegalArgumentException("Subprojekt ikke fundet");
        }
        if (task == null) {
            throw new IllegalArgumentException("Task ikke fundet");
        }
        if (subproject.getTasks().contains(task)) {
            subproject.getTasks().remove(task); // Bi-directional. Sætter taskens subproject til null. Se subproject class.
            subprojectRepository.save(subproject);
        }
    }
}
