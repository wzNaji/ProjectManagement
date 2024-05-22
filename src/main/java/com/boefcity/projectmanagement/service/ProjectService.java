package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.Subproject;

import java.util.List;

public interface ProjectService {

    void createProject (Project project);

    Project findProjectById (Long id);

    void deleteProjectById(Long id);

    List<Project> findAllProjects();

    void assignUsersToProject (Long projectID, Long userID);


    boolean isUserAssignedToProject(Long projectID, Long userID);

    void assignSubprojectToProject(Subproject subproject, Long projectId);

    void removeUserFromProject(Long userId, Long projectId);

    void editProject(Long projectId, Project projectDetails);
}
