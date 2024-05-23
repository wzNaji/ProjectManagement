package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Project;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindProjectByIdNative() {
        Project project = new Project();
        project.setProjectName("Test Project");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRole(Role.MANAGER);

        project = projectRepository.save(project);

        Project foundProject = projectRepository.findProjectByIdNative(project.getProjectId());
        assertNotNull(foundProject);
        assertEquals(project.getProjectName(), foundProject.getProjectName());
    }

    @Test
    public void testExistsByProjectIdAndUsersUserId() {
        Project project = new Project();
        project.setProjectName("Test Project");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRole(Role.MANAGER);

        user = userRepository.save(user);
        project = projectRepository.save(project);

        project.getUsers().add(user);
        project = projectRepository.save(project);

        boolean exists = projectRepository.existsByProjectIdAndUsersUserId(project.getProjectId(), user.getUserId());
        assertTrue(exists);
    }
}
