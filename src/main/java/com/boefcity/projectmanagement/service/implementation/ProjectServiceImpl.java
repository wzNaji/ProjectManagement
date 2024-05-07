package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.repository.ProjectRepository;
import com.boefcity.projectmanagement.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

@Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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
    public void deleteById(Long id) {
    projectRepository.deleteById(id);
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
}