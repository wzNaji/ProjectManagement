package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Subproject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubprojectRepository extends JpaRepository<Subproject, Long> {

    // Se UserRepository for details
    @Query(value = "SELECT * FROM subproject WHERE subproject_id = :subprojectId", nativeQuery = true)
    Subproject findSubprojectByIdNative(@Param("subprojectId") Long subProjectId);

}
