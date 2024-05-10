package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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
    public void deleteById(Long projectId) {
        Project projectToDelete = projectRepository.findProjectByIdNative(projectId);
        if (projectToDelete != null) {
            projectToDelete.removeAllUsers();
            projectRepository.delete(projectToDelete);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project update(Long projectId, Project projectDetails) {
        Project projectToUpdate = projectRepository.findProjectByIdNative(projectId);

        if (projectToUpdate == null) {
            throw new EntityNotFoundException("User not found for id: " + projectId);
        }

        projectToUpdate.setProjectName(projectDetails.getProjectName());
        projectToUpdate.setProjectDescription(projectDetails.getProjectDescription());
        projectToUpdate.setProjectStartDate(projectDetails.getProjectStartDate());
        projectToUpdate.setProjectEndDate(projectDetails.getProjectEndDate());


        return projectRepository.save(projectToUpdate);
    }

    // se om vi kan bruge utility metoder i projects klassen eller user klassen.
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


}