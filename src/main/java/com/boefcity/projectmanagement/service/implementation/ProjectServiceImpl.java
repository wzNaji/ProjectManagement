package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.TaskRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }


    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    @Override
    public Project findById(Long id) {
        return projectRepository.findProjectByIdNative(id);
    }

    @Override
    @Transactional
    public void deleteById(Long projectId) {
        Project projectToDelete = projectRepository.findProjectByIdNative(projectId);
        if (projectToDelete != null) {
            // Detacher users fra projektet
            projectToDelete.removeAllUsers();

            // Laver en kopi for at undgå listen bliver null.
            List<Task> tasksToDelete = new ArrayList<>(projectToDelete.getTasks());
            projectToDelete.removeAllTasks();

            // Sletter tasks fra databasen
            if (!tasksToDelete.isEmpty()) {
                taskRepository.deleteAll(tasksToDelete);
            }

            projectRepository.delete(projectToDelete);
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project assignUsersToProject(Long projectID, Long userID) {
        Project project = projectRepository.findProjectByIdNative(projectID);
        User userToAssign = userRepository.findUserByIdNative(userID);

        if (project != null && userToAssign != null) {
            project.getUsers().add(userToAssign);
            userToAssign.getProjects().add(project);
            userRepository.save(userToAssign);

            return projectRepository.save(project);
        } else {
            throw new RuntimeException("Project or User not found");
        }
    }
    public boolean isUserAssignedToProject(Long projectId, Long userId) {
        return projectRepository.existsByProjectIdAndUsersUserId(projectId, userId);
    }

    @Transactional
    @Override
    public Project assignTaskToProject(Task task, Long projectId) {

        Project project = projectRepository.findProjectByIdNative(projectId);
        Task taskToAssign = taskRepository.findTaskByIdNative(task.getTaskId());

        if (project == null || taskToAssign == null) {
            throw new IllegalArgumentException("Project or Task not found");
        }

        // Default values '0.0' hvis cost eller hours er 'null'
        double taskCost = (task.getTaskCost() != null) ? task.getTaskCost() : 0.0;
        double taskHours = (task.getTaskHours() != null) ? task.getTaskHours() : 0.0;

        // Opdatér project cost
        double projectCost = (project.getProjectCost() != null) ? project.getProjectCost() : 0.0;
        project.setProjectCost(projectCost + taskCost);

        // Opdatér project hours
        double projectHours = (project.getProjectActualdHours() != null) ? project.getProjectActualdHours() : 0.0;
        project.setProjectActualdHours(projectHours + taskHours);

        project.addTaskToProject(task);

        return projectRepository.save(project);
    }

    @Transactional
    @Override
    public void removeUserFromProject(Long userId, Long projectId) {

        User user = userRepository.findUserByIdNative(userId);
        Project project = projectRepository.findProjectByIdNative(projectId);

        if (user == null || project == null) {
            throw new IllegalArgumentException("Could not find user or project. userId: " + userId + ", projectId: " + projectId);
        }
            user.removeProject(project); /* Fjerner projektet fra useren og fjerner useren fra projektet.
                                                          Se 'removeProject' i User klassen */
    }

    @Transactional
    @Override
    public Project updateProject(Long projectId, Project projectDetails) {

        Project projectToUpdate = projectRepository.findProjectByIdNative(projectId);
        if (projectToUpdate == null) {
            throw new EntityNotFoundException("User not found for id: " + projectId);
        }

        projectToUpdate.setProjectName(projectDetails.getProjectName());
        projectToUpdate.setProjectDescription(projectDetails.getProjectDescription());
        projectToUpdate.setProjectStartDate(projectDetails.getProjectStartDate());
        projectToUpdate.setProjectEndDate(projectDetails.getProjectEndDate());
        projectToUpdate.setProjectBudget(projectDetails.getProjectBudget());
        projectToUpdate.setProjectEstimatedHours(projectDetails.getProjectEstimatedHours());

        return projectRepository.save(projectToUpdate);
    }


}