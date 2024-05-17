package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Subproject;

import java.util.List;

public interface SubprojectService {

    void createSubproject (Subproject subproject);

    Subproject findBySubprojectId(Long subprojectId);

    void updateSubproject(Long subprojectId, Subproject subproject);

    void deleteSubproject(Long subprojectId, Long projectId);

}
