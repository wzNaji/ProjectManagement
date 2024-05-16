package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.SubProject;

import java.util.List;

public interface ProjectService {

    Project createProject (Project project);

    Project findById (Long id);

    void deleteById(Long id);

    List<Project> findAll();

    Project assignUsersToProject (Long projectID, Long userID);

    boolean isUserAssignedToProject(Long projectID, Long userID);

    Project assignSubProjectToProject(SubProject subProject, Long projectId);

    void removeUserFromProject(Long userId, Long projectId);

    Project updateProject(Long projectId, Project projectDetails);
}
