package com.boefcity.projectmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "username", nullable = false, unique = true)
        private String username;

        @Column(name = "email")
        private String email;

        @Enumerated(EnumType.STRING)
        @Column(name = "user_role", nullable = false)
        private Role userRole;

        @Column(name = "password", nullable = false)
        @ToString.Exclude
        private String password;

        @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
        @JoinTable(
                name = "user_projects",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "project_id")
        )
        @ToString.Exclude
        private List<Project> projects = new ArrayList<>();

        public void addProject(Project project) {
                projects.add(project);
                project.getUsers().add(this);
        }

        public void removeProject(Project project) {
                projects.remove(project);
                project.getUsers().remove(this);
        }

        public void removeAllProjects() {
                for (Project project : new ArrayList<>(projects)) {
                        removeProject(project);
                }
        }
}
