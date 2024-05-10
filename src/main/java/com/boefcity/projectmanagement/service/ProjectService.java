package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Project;

import java.util.List;

public interface ProjectService {

    Project createProject (Project project);

    Project findById (Long id);

    void deleteById(Long id);

    List<Project> findAll();

    Project update (Long id, Project projectDetails);

    Project assignUsersToProject (Long projectID, Long userID);

    boolean isUserAssignedToProject(Long projectID, Long userID);

}
