package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.SubProject;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubProjectRepository;
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
    private final SubProjectRepository subProjectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, SubProjectRepository subProjectRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.subProjectRepository = subProjectRepository;
    }


    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

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
            List<SubProject> subProjectsToDelete = new ArrayList<>(projectToDelete.getSubProjects());
            projectToDelete.removeAllSubProjects();

            // Sletter sub projects fra databasen
            if (!subProjectsToDelete.isEmpty()) {
                subProjectRepository.deleteAll(subProjectsToDelete);
            }

            projectRepository.delete(projectToDelete);
        }
    }


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
    public Project assignSubProjectToProject(SubProject subProject, Long projectId) {

        Project project = projectRepository.findProjectByIdNative(projectId);
        SubProject subProjectToAssign = subProjectRepository.findSubProjectByIdNative(subProject.getSubProjectId());

        if (project == null || subProjectToAssign == null) {
            throw new IllegalArgumentException("Project or sub project not found");
        }

        // Default values '0.0' hvis cost eller hours er 'null'
        double subProjectCost = (subProject.getSubProjectCost() != null) ? subProject.getSubProjectCost() : 0.0;
        double subProjectHours = (subProject.getSubProjectHours() != null) ? subProject.getSubProjectHours() : 0.0;

        // Opdatér project cost
        double projectCost = (project.getProjectCost() != null) ? project.getProjectCost() : 0.0;
        project.setProjectCost(projectCost + subProjectCost);

        // Opdatér project hours
        double projectHours = (project.getProjectActualdHours() != null) ? project.getProjectActualdHours() : 0.0;
        project.setProjectActualdHours(projectHours + projectHours);

        project.addSubProjectToProject(subProject);

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