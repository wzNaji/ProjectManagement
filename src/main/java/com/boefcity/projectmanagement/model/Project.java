package com.boefcity.projectmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "project_start_date")
    private LocalDateTime projectStartDate;

    @Column(name = "project_end_date")
    private LocalDateTime projectEndDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "project_tasks",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> tasks = new ArrayList<>();

    @Column(name = "project_budget", nullable = true)
    private Double projectBudget;

    @Column(name = "project_cost", nullable = true)
    private Double projectCost;

    @Column(name = "project_esitmated_hours", nullable = true)
    private Double projectEstimatedHours;

    @Column(name = "project_actual_hours", nullable = true)
    private Double projectActualdHours;

    public void addUser(User user) {
        users.add(user);
        user.getProjects().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getProjects().remove(this);
    }

    public void removeAllUsers() {
        for (User user : new ArrayList<>(users)) {
            removeUser(user);
        }
    }
    public void addTaskToProject(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        if (tasks.remove(task)) { // Only nullify the project reference if the task was successfully removed
            task.setProject(null);
        }
    }

    public void removeAllTasks() {
        // Iterate over a copy of the task list to avoid ConcurrentModificationException
        for (Task task : new ArrayList<>(tasks)) {
            removeTask(task);
        }
        tasks.clear(); // Optionally clear the tasks list after all tasks have been disassociated
    }


}
