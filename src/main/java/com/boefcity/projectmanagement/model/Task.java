package com.boefcity.projectmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false, unique = true)
    private Long taskId;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "task_description", nullable = true)
    private String taskDescription;


    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = true)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,})
    @JoinColumn(name = "subproject_id", nullable = true)

    private Subproject subproject;



}
