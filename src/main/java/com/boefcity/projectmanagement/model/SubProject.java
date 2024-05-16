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
@Table(name = "subProject")
@AllArgsConstructor
@NoArgsConstructor
public class SubProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subProject_id", nullable = false, unique = true)
    private Long subProjectId;

    @Column(name = "subProject_name", nullable = false)
    private String subProjectName;

    @Column(name = "subProject_description", nullable = true)
    private String subProjectDescription;

    @Column(name = "subProject_start_date", nullable = true)
    private LocalDateTime subProjectStartDate;

    @Column(name = "subProject_due_date", nullable = true)
    private LocalDateTime subProjectDueDate;

    @Enumerated(EnumType.STRING) // Enum bliver gemt som en String i databasen.
    @Column(name = "priority_level", nullable = true)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private Status status;

    @Column(name = "subProject_cost", nullable = true)
    private Double subProjectCost;

    @Column(name = "subProject_hours", nullable = true)
    private Double subProjectHours;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "subProject_users",
            joinColumns = @JoinColumn(name = "subProject_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,})
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;
}
