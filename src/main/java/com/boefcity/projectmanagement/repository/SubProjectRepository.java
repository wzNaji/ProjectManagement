package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.SubProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubProjectRepository extends JpaRepository<SubProject, Long> {

    // Se UserRepository for details
    @Query(value = "SELECT * FROM subProject WHERE subProject_id = :subProjectId", nativeQuery = true)
    SubProject findSubProjectByIdNative(@Param("subProjectId") Long subProjectId);

    SubProject findBySubProjectName(String subProjectName);

}
