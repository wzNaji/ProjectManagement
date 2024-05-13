package com.boefcity.projectmanagement.service.implementation;

import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import com.boefcity.projectmanagement.repository.UserRepository;
import com.boefcity.projectmanagement.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User findUserById(Long id) {
        return userRepository.findUserByIdNative(id);
    }

    @Override
    public User createUser(User user) {
        user.setUserRole(Role.WORKER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User userDetails) {
        User userToUpdate = userRepository.findUserByIdNative(userId);
        if (userToUpdate == null) {
            throw new EntityNotFoundException("User not found for id: " + userId);
        }
        userToUpdate.setUsername(userDetails.getUsername());
        userToUpdate.setEmail(userDetails.getEmail());
        userToUpdate.setUserRole(userDetails.getUserRole());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Encode new password
        }
        return userRepository.save(userToUpdate);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findUserByIdNative(userId);
        if (userToDelete != null ) {
            userToDelete.getTasks().clear();
            userToDelete.removeAllProjects();
            userRepository.deleteById(userId);
        }
        else throw new RuntimeException("User was not found");
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean checkLogin(String username, String password) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

}
