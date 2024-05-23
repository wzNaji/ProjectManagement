package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
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
    private final SubprojectRepository subprojectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, SubprojectRepository subprojectRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.subprojectRepository = subprojectRepository;
    }


    @Override
    public void createProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("projektet blev ikke fundet");
        }
        projectRepository.save(project);
    }

    @Override
    public Project findProjectById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("projekt Id blev ikke fundet");
        }
        Project project = projectRepository.findProjectByIdNative(id);
        if (project == null) {
            throw new RuntimeException("Projektet blev ikke fundet");
        }
        return project;
    }

    @Override
    @Transactional
    public void deleteProjectById(Long projectId) {
        Project projectToDelete = projectRepository.findProjectByIdNative(projectId);
        if (projectToDelete != null) {
            // Detacher users fra projektet for at kunne slette projektet uden komplikationer ved sletning
            projectToDelete.removeAllUsers();

            // Laver en kopi af underprojekter listen for at undgå problemer med ændringer under sletning
            List<Subproject> subprojectsToDelete = new ArrayList<>(projectToDelete.getSubprojects());
            projectToDelete.removeAllSubprojects();

            // Sletter underprojekter fra databasen - if statement for at undgå unødige database kald
            if (!subprojectsToDelete.isEmpty()) {
                subprojectRepository.deleteAll(subprojectsToDelete);
            }

            projectRepository.delete(projectToDelete);
        }
    }


    @Override
    public List<Project> findAllProjects() {
        List<Project> projectList = projectRepository.findAll();
        if (projectList == null) {
            throw new RuntimeException("Fejl under fetching af projekter");
        }
        return projectList;
    }
    @Transactional
    @Override
    public void assignUsersToProject(Long projectID, Long userID) {
        Project project = projectRepository.findProjectByIdNative(projectID);
        User userToAssign = userRepository.findUserByIdNative(userID);

        if (project == null) {
            throw new RuntimeException("Projektet blev ikke fundet");
        }

        if (userToAssign == null) {
            throw new RuntimeException("Brugeren blev ikke fundet");
        }

        // Brug metoden i Project-klassen til at tilføje brugeren og opdatere relationen - Bi-Directional
        project.addUser(userToAssign);

        userRepository.save(userToAssign);
        projectRepository.save(project);
    }

    public boolean isUserAssignedToProject(Long projectId, Long userId) {

        if (projectId == null) {
            throw new IllegalArgumentException("Projekt Id blev ikke fundet");
        }
        if (userId == null) {
            throw new IllegalArgumentException("Brugerens Id blev ikke fundet");
        }

        return projectRepository.existsByProjectIdAndUsersUserId(projectId, userId);
    }

    @Transactional
    @Override
    public void assignSubprojectToProject(Subproject subproject, Long projectId) {

        Project project = projectRepository.findProjectByIdNative(projectId);
        Subproject subprojectToAssign = subprojectRepository.findSubprojectByIdNative(subproject.getSubprojectId());

        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        if (subprojectToAssign == null) {
            throw new IllegalArgumentException("Subproject not found");
        }

        // Default values '0.0' hvis cost eller hours er 'null'
        double subprojectCost = (subproject.getSubprojectCost() != null) ? subproject.getSubprojectCost() : 0.0;
        double subprojectHours = (subproject.getSubprojectHours() != null) ? subproject.getSubprojectHours() : 0.0;

        // Opdaterer projekt cost
        double projectCost = (project.getProjectCost() != null) ? project.getProjectCost() : 0.0;
        project.setProjectCost(projectCost + subprojectCost);

        // Opdaterer projekt hours
        double projectHours = (project.getProjectActualdHours() != null) ? project.getProjectActualdHours() : 0.0;
        project.setProjectActualdHours(projectHours + subprojectHours);

        project.addSubprojectToProject(subproject);

        projectRepository.save(project);
    }

    @Transactional
    @Override
    public void removeUserFromProject(Long userId, Long projectId) {

        User user = userRepository.findUserByIdNative(userId);
        Project project = projectRepository.findProjectByIdNative(projectId);

        if (user == null || project == null) {
            throw new IllegalArgumentException("Could not find user or project. userId: " + userId + ", projectId: " + projectId);
        }
            user.removeProject(project); // Fjerner projektet fra useren og fjerner useren fra projektet. Se 'removeProject' i User klassen
            userRepository.save(user);
    }

    @Transactional
    @Override
    public void editProject(Long projectId, Project projectDetails) {

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

        projectRepository.save(projectToUpdate);
    }


}