package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.repository.TaskRepository;
import com.boefcity.projectmanagement.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    @Transactional(readOnly = true)
    @Override
    public Task findByTaskId(Long taskId) {
        return taskRepository.findTaskByIdNative(taskId);
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        Task taskToDelete = taskRepository.findTaskByIdNative(taskId);
        if (taskToDelete != null) {
            //remove all users
            taskRepository.delete(taskToDelete);
        }

    }
    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTask() {
        return taskRepository.findAll();
    }

    @Override
    public Task update(Long taskId, Task taskDetails) {
            Task taskToUpdate = taskRepository.findTaskByIdNative(taskId);

            if (taskToUpdate == null) {
                throw new EntityNotFoundException("User not found for id: " + taskId);
            }

            taskToUpdate.setTaskName(taskDetails.getTaskName());
            taskToUpdate.setTaskDescription(taskDetails.getTaskDescription());
            taskToUpdate.setTaskStartDate(taskDetails.getTaskStartDate());
            taskToUpdate.setTaskDueDate(taskDetails.getTaskDueDate());
            taskToUpdate.setPriorityLevel(taskDetails.getPriorityLevel());
            taskToUpdate.setStatus(taskDetails.getStatus());

            // tjek denne - måske det skal ændres i Model... tænker ManyToMany, fordi 1 task kan have mange users og vice versa.
            //taskToUpdate.setAssignedUser(taskDetails.getAssignedUser());


            return taskRepository.save(taskDetails);
        }


    @Override
    public Task assignUsersToTask(Long taskId, Long userID) {
        return null;
    }

    @Override
    public boolean isUserAssignedToTask(Long taskId, Long userID) {
        return false;
    }
}