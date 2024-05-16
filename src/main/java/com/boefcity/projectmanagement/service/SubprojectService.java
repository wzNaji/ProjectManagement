package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Subproject;

import java.util.List;

public interface SubprojectService {

    Subproject createSubproject (Subproject subproject);

    List<Subproject> findAllSubproject();

    Subproject findBySubprojectId(Long subprojectId);

    void deleteSubproject(Long subprojectId, Long projectId);

    void updateSubproject(Long subprojectId, Subproject subproject);
}
