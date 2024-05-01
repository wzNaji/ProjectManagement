package com.boefcity.projectmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data // Getters, setters, toString osv.
@Entity // Mapper 'task' entity/klasse til en tabel hvor name = "items" i databasen
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false, unique = true)
    private Long taskId;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "priority_level_id", nullable = false)
    private PriorityLevel priorityLevel;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = false)
    private User assignedUser;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;



}
