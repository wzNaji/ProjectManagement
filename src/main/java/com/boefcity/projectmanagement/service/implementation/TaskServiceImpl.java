package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.TaskRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTask() {
        return taskRepository.findAll();
    }

    @Transactional
    @Override
    public Task updateTask(Long taskId, Task taskDetails) {

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
            taskToUpdate.setTaskCost(taskDetails.getTaskCost());
            taskToUpdate.setTaskHours(taskDetails.getTaskHours());

            return taskRepository.save(taskToUpdate);
    }


    @Override
    public Task assignUsersToTask(Long taskId, Long userID) {
        return null;
    }

    @Override
    public boolean isUserAssignedToTask(Long taskId, Long userID) {
        return false;
    }
    @Override
    @Transactional
    public void deleteTask(Long taskId, Long projectId) {
        Project project = projectRepository.findProjectByIdNative(projectId);
        Task task = taskRepository.findTaskByIdNative(taskId);

            // undgå unødige database handlinger
        if (project == null || task == null) {
            throw new IllegalArgumentException("Project or Task not found");
        }

        if (project.getTasks().contains(task)) {

            // Fjerner taskens hours fra projektets samlede hours.
            Double taskHours = task.getTaskHours();
            Double currentProjectHours = project.getProjectActualdHours() != null ? project.getProjectActualdHours() : 0;
            project.setProjectActualdHours(currentProjectHours - taskHours);

            // Fjerner taskens cost fra projektets samlede cost.
            Double taskCost = task.getTaskCost();
            Double currentProjectCost = project.getProjectCost() != null ? project.getProjectCost() : 0;
            project.setProjectCost(currentProjectCost - taskCost);

            project.getTasks().remove(task); // Detach task fra project
            projectRepository.save(project);
        }

        //remove task from users taskList når users har assigned tasks.

        taskRepository.delete(task);
    }

}
