package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.SubProject;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.repository.SubProjectRepository;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.SubProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubProjectServiceImpl implements SubProjectService {

    private final SubProjectRepository subProjectRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public SubProjectServiceImpl(SubProjectRepository subProjectRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.subProjectRepository = subProjectRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SubProject createSubProject(SubProject subProject) {
        return subProjectRepository.save(subProject);
    }

    @Transactional
    @Override
    public List<SubProject> findAllSubProject() {
        return subProjectRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteSubProject(Long subProjectId, Long projectId) {
        Project project = projectRepository.findProjectByIdNative(projectId);
        SubProject subProject = subProjectRepository.findSubProjectByIdNative(subProjectId);

        // undgå unødige database handlinger
        if (project == null || subProject == null) {
            throw new IllegalArgumentException("Project or sub project not found");
        }

        if (project.getSubProjects().contains(subProject)) {
            // Fjerner sub projektets hours fra projektets samlede hours.
            Double subProjectHours = subProject.getSubProjectHours();
            Double currentProjectHours = project.getProjectActualdHours() != null ? project.getProjectActualdHours() : 0;
            project.setProjectActualdHours(currentProjectHours - subProjectHours);

            // Fjerner sub projektets cost fra projektets samlede cost.
            Double subProjectCost = subProject.getSubProjectCost();
            Double currentProjectCost = project.getProjectCost() != null ? project.getProjectCost() : 0;
            project.setProjectCost(currentProjectCost - subProjectCost);

            project.getSubProjects().remove(subProject); // Detach sub project fra project
            projectRepository.save(project);
        }

        subProjectRepository.delete(subProject);
    }

    @Transactional
    @Override
    public SubProject findBySubProjectId(Long subProjectId) {
        return subProjectRepository.findSubProjectByIdNative(subProjectId);
    }


    @Transactional
    @Override
    public void updateSubProject(Long subProjectId, SubProject subProjectDetails) {
        SubProject subprojectToUpdate = subProjectRepository.findSubProjectByIdNative(subProjectId);
        if (subprojectToUpdate == null) {
            throw new EntityNotFoundException("Sub project not found for id: " + subProjectId);
        }

        subprojectToUpdate.setSubProjectName(subProjectDetails.getSubProjectName());
        subprojectToUpdate.setSubProjectDescription(subProjectDetails.getSubProjectDescription());
        subprojectToUpdate.setSubProjectStartDate(subProjectDetails.getSubProjectStartDate());
        subprojectToUpdate.setSubProjectDueDate(subProjectDetails.getSubProjectDueDate());
        subprojectToUpdate.setPriorityLevel(subProjectDetails.getPriorityLevel());
        subprojectToUpdate.setStatus(subProjectDetails.getStatus());
        subprojectToUpdate.setSubProjectCost(subProjectDetails.getSubProjectCost());
        subprojectToUpdate.setSubProjectCost(subProjectDetails.getSubProjectHours());

        subProjectRepository.save(subprojectToUpdate);
    }

}
