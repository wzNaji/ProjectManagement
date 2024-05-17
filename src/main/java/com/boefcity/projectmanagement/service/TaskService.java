package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Task;

public interface TaskService {

    void createTask (Task task);

    Task findByTaskId(Long taskId);

    void editTask(Long taskId, Task taskDetails);

    void deleteTask(Long taskId, Long subprojectId);

}
