package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask (Task task);

    Task findByTaskId (Long taskId);

    void deleteTask(Long taskId, Long projectId);

    List<Task> findAllTask();

    Task update (Long taskId, Task taskDetails);

    Task assignUsersToTask (Long taskId, Long userID);

    boolean isUserAssignedToTask(Long taskId, Long userID);
}
