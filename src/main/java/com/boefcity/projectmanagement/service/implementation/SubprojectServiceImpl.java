package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubprojectRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.SubprojectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubprojectServiceImpl implements SubprojectService {

    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public SubprojectServiceImpl(SubprojectRepository subprojectRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.subprojectRepository = subprojectRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Subproject createSubproject(Subproject subproject) {
        return subprojectRepository.save(subproject);
    }

    @Transactional
    @Override
    public List<Subproject> findAllSubproject() {
        return subprojectRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteSubproject(Long subprojectId, Long projectId) {
        Project project = projectRepository.findProjectByIdNative(projectId);
        Subproject subproject = subprojectRepository.findSubprojectByIdNative(subprojectId);

        // undgå unødige database handlinger
        if (project == null || subproject == null) {
            throw new IllegalArgumentException("Project or subproject not found");
        }

        if (project.getSubprojects().contains(subproject)) {
            // Fjerner subprojektets hours fra projektets samlede hours.
            Double subprojectHours = subproject.getSubprojectHours();
            Double currentProjectHours = project.getProjectActualdHours() != null ? project.getProjectActualdHours() : 0;
            project.setProjectActualdHours(currentProjectHours - subprojectHours);

            // Fjerner subprojektets cost fra projektets samlede cost.
            Double subprojectCost = subproject.getSubprojectCost();
            Double currentProjectCost = project.getProjectCost() != null ? project.getProjectCost() : 0;
            project.setProjectCost(currentProjectCost - subprojectCost);

            project.getSubprojects().remove(subproject); // Detach sub project fra project
            projectRepository.save(project);
        }

        subprojectRepository.delete(subproject);
    }

    @Transactional
    @Override
    public Subproject findBySubprojectId(Long subprojectId) {
        return subprojectRepository.findSubprojectByIdNative(subprojectId);
    }


    @Transactional
    @Override
    public void updateSubproject(Long subprojectId, Subproject subprojectDetails) {
        Subproject subprojectToUpdate = subprojectRepository.findSubprojectByIdNative(subprojectId);
        if (subprojectToUpdate == null) {
            throw new EntityNotFoundException("Subproject not found for id: " + subprojectId);
        }

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
