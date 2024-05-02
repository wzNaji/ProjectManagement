package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
