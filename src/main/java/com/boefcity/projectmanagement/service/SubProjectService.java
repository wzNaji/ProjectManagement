package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.SubProject;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface SubProjectService {

    SubProject createSubProject (SubProject subProject);

    List<SubProject> findAllSubProject();

    SubProject findBySubProjectId(Long subProjectId);

    void deleteSubProject(Long subProjectId, Long projectId);

    void updateSubProject(Long subProjectId, SubProject subProject);
}
