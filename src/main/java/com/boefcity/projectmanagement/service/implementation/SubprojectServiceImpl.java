package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.repository.TaskRepository;
import com.boefcity.projectmanagement.service.SubprojectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubprojectServiceImpl implements SubprojectService {

    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public SubprojectServiceImpl(SubprojectRepository subprojectRepository,
                                 ProjectRepository projectRepository,
                                 TaskRepository taskRepository) {
        this.subprojectRepository = subprojectRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void createSubproject(Subproject subproject) {
        if (subproject == null) {
            throw new IllegalArgumentException("Subprojekt blev ikke fundet");
        }

        try {
            subprojectRepository.save(subproject);
        } catch (Exception e) {
            throw new RuntimeException("Subprojektet blev ikke gemt");
        }
    }

    @Override
    @Transactional
    public void deleteSubproject(Long subprojectId, Long projectId) {
        Project project = projectRepository.findProjectByIdNative(projectId);
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);

        // Error handling for at undgå unødige database handlinger
        if (project == null) {
            throw new IllegalArgumentException("Projekt ikke fundet");
        }

        if (subproject == null) {
            throw new IllegalArgumentException("Subprojekt ikke fundet");
        }

        if (project.getSubprojects().contains(subproject)) {
            // Fjerner subprojektets hours fra projektets samlede hours, så projektHours opdateres efter deleteSubproject
            Double subprojectHours = subproject.getSubprojectHours();
            Double currentProjectHours = project.getProjectActualdHours() != null ? project.getProjectActualdHours() : 0;
            project.setProjectActualdHours(currentProjectHours - subprojectHours);

            // Fjerner subprojektets cost fra projektets samlede cost, så projektCost opdateres efter deleteSubproject.
            Double subprojectCost = subproject.getSubprojectCost();
            Double currentProjectCost = project.getProjectCost() != null ? project.getProjectCost() : 0;
            project.setProjectCost(currentProjectCost - subprojectCost);
            //Fjerner subprojektet fra projektet, så det efterfølgende kan slettes
            project.getSubprojects().remove(subproject);
            projectRepository.save(project);
        }

        subprojectRepository.delete(subproject);
    }

    @Transactional
    @Override
    public void assignTaskToSubproject(Task task, Long subprojectId) {
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);
        Task taskToAssign = taskRepository.findTaskByIdNative(task.getTaskId());

        if (subproject == null) {
            throw new IllegalArgumentException("Subprojektet blev ikke fundet");
        }

        if (taskToAssign == null) {
            throw new IllegalArgumentException("Task blev ikke fundet");
        }

        subproject.addTaskToSubproject(task);
        subprojectRepository.save(subproject);
    }

    @Override
    public Subproject findBySubprojectId(Long subprojectId) {
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);

        if (subproject == null) {
            throw new IllegalArgumentException("Subprojekt med ID: " + subprojectId + " findes ikke.");
        }
        return subproject;
    }


    @Transactional
    @Override
    public void updateSubproject(Long subprojectId, Subproject subprojectDetails) {
        Subproject subprojectToUpdate = subprojectRepository.findSubprojectByIdNative(subprojectId);

        if (subprojectToUpdate == null) {
            throw new EntityNotFoundException("Subproject with ID: " + subprojectId + " does not exist.");
        }

        // Opdaterer subprojektet med nye værdier
        subprojectToUpdate.setSubprojectName(subprojectDetails.getSubprojectName());
        subprojectToUpdate.setSubprojectDescription(subprojectDetails.getSubprojectDescription());
        subprojectToUpdate.setSubprojectStartDate(subprojectDetails.getSubprojectStartDate());
        subprojectToUpdate.setSubprojectDueDate(subprojectDetails.getSubprojectDueDate());
        subprojectToUpdate.setPriorityLevel(subprojectDetails.getPriorityLevel());
        subprojectToUpdate.setStatus(subprojectDetails.getStatus());
        subprojectToUpdate.setSubprojectCost(subprojectDetails.getSubprojectCost());
        subprojectToUpdate.setSubprojectHours(subprojectDetails.getSubprojectHours());

        subprojectRepository.save(subprojectToUpdate);

        // Opdaterer tilknyttede projekt med subprojektets nye vost og hour værdier - Se 'updateProjectCostsAndHours'
        Project parentProject = subprojectToUpdate.getProject();
        if (parentProject != null) {
            updateProjectCostsAndHours(parentProject);
            projectRepository.save(parentProject);
        }
    }

    private void updateProjectCostsAndHours(Project project) {
        List<Subproject> subprojects = project.getSubprojects();
        double totalCost = 0;
        double totalHours = 0;

        for (Subproject subproject : subprojects) {
            totalCost += subproject.getSubprojectCost();
            totalHours += subproject.getSubprojectHours();
        }

        project.setProjectCost(totalCost);
        project.setProjectActualdHours(totalHours);
    }

}
