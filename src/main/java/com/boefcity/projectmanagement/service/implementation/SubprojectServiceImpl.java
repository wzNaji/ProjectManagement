package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.service.SubprojectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubprojectServiceImpl implements SubprojectService {

    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;

    public SubprojectServiceImpl(SubprojectRepository subprojectRepository, ProjectRepository projectRepository) {
        this.subprojectRepository = subprojectRepository;
        this.projectRepository = projectRepository;
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

    @Override
    public Subproject findBySubprojectId(Long subprojectId) {
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);
        // Error handling for at undgå unødige database handlinger
        if (subproject == null) {
            throw new IllegalArgumentException("Subprojekt med ID: " + subprojectId + " findes ikke.");
        }
        return subproject;
    }


    @Transactional
    @Override
    public void updateSubproject(Long subprojectId, Subproject subprojectDetails) {
        Subproject subprojectToUpdate = subprojectRepository.findSubprojectByIdNative(subprojectId);

        // Error handling for at undgå unødige database handlinger
        if (subprojectToUpdate == null) {
            throw new EntityNotFoundException("Subprojekt med ID: " + subprojectId + " findes ikke.");
        }
        //Setter det eksisterende subprojekts attributer til nye værdier, så vi efterfølgende kan opdatere og gemme.
        subprojectToUpdate.setSubprojectName(subprojectDetails.getSubprojectName());
        subprojectToUpdate.setSubprojectDescription(subprojectDetails.getSubprojectDescription());
        subprojectToUpdate.setSubprojectStartDate(subprojectDetails.getSubprojectStartDate());
        subprojectToUpdate.setSubprojectDueDate(subprojectDetails.getSubprojectDueDate());
        subprojectToUpdate.setPriorityLevel(subprojectDetails.getPriorityLevel());
        subprojectToUpdate.setStatus(subprojectDetails.getStatus());
        subprojectToUpdate.setSubprojectCost(subprojectDetails.getSubprojectCost());
        subprojectToUpdate.setSubprojectCost(subprojectDetails.getSubprojectHours());

        subprojectRepository.save(subprojectToUpdate);
    }

}
