package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.Subproject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SubprojectRepositoryTest {

    @Autowired
    private SubprojectRepository subprojectRepository;

    @Test
    public void testFindSubprojectByIdNative() {
        Subproject subproject = new Subproject();
        subproject.setSubprojectName("Test Subproject");

        subproject = subprojectRepository.save(subproject);

        Subproject foundSubproject = subprojectRepository.findSubprojectByIdNative(subproject.getSubprojectId());
        assertNotNull(foundSubproject);
        assertEquals(subproject.getSubprojectName(), foundSubproject.getSubprojectName());
    }
}
