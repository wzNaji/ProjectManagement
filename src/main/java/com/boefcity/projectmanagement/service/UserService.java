package com.boefcity.projectmanagement.service;

import com.boefcity.projectmanagement.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findUserById(Long id);
    User createUser(User user);
    void editUser(Long userId, User userDetails);
    void deleteUser(Long userId);
    List<User> findAllUsers();
    boolean checkLogin(String username, String password);
    Optional<User> findUserByUsername(String username); // fjern optional når repo er refactored.

}
