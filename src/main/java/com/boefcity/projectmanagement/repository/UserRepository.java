package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
