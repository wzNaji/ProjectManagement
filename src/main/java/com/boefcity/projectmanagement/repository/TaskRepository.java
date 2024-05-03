package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Task;
import com.boefcity.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Se UserRepository for details
    @Query(value = "SELECT * FROM task WHERE task_id = :taskId", nativeQuery = true)
    Task findTaskByIdNative(@Param("taskId") Long taskId);

}
