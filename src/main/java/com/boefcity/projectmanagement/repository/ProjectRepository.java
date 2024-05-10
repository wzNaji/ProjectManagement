package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Se UserRepository for details
    @Query(value = "SELECT * FROM project WHERE project_id = :projectId", nativeQuery = true)
    Project findProjectByIdNative(@Param("projectId") Long projectId);

    boolean existsByProjectIdAndUsersUserId(Long projectId, Long userId);

}
