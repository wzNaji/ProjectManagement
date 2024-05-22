package com.boefcity.projectmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // Getters, setters, toString osv.
@Entity // Maps 'task' entity/class to a table named "task" in the database
@Table(name = "subproject")
@AllArgsConstructor
@NoArgsConstructor
public class Subproject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subproject_id", nullable = false, unique = true)
    private Long subprojectId;

    @Column(name = "subproject_name", nullable = false)
    private String subprojectName;

    @Column(name = "subproject_description", nullable = true)
    private String subprojectDescription;

    @Column(name = "subproject_start_date", nullable = true)
    private LocalDateTime subprojectStartDate;

    @Column(name = "subproject_due_date", nullable = true)
    private LocalDateTime subprojectDueDate;

    @Enumerated(EnumType.STRING) // Enum bliver gemt som en String i databasen.
    @Column(name = "priority_level", nullable = true)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private Status status;

    @Column(name = "subproject_cost", nullable = true)
    private Double subprojectCost;

    @Column(name = "subproject_hours", nullable = true)
    private Double subprojectHours;


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "subproject_tasks",
            joinColumns = @JoinColumn(name = "subproject_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,})
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;


    public void removeTaskFromSubproject(Task task) {
        if (tasks.remove(task)) {
            task.setSubproject(null);
        }
    }
    public void addTaskToSubproject(Task task) {
        tasks.add(task);
        task.setSubproject(this);
    }
}
