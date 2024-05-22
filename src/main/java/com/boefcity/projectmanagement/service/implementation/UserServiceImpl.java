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
        // Error handling for at undgå unødige database handlinger
        if(id==null){
            throw new IllegalArgumentException("Bruger ID blev ikke fundet");
        }
        User userToFind = userRepository.findUserByIdNative(id);

        if(userToFind==null){
            throw new RuntimeException("Brugeren blev ikke fundet");
        }
        return userToFind;
    }

    @Override
    public User createUser(User user) {
        user.setUserRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void editUser(Long userId, User userDetails) {
        User userToUpdate = userRepository.findUserByIdNative(userId);
        // Error handling for at undgå unødige database handlinger
        if (userToUpdate == null) {
            throw new EntityNotFoundException("Bruger blev ikke fundet med ID: " + userId);
        }
//Setter den eksisterende brugers attributer til nye værdier, så vi efterfølgende kan opdatere og gemme.
        userToUpdate.setUsername(userDetails.getUsername());
        userToUpdate.setEmail(userDetails.getEmail());
        userToUpdate.setUserRole(userDetails.getUserRole());

        userRepository.save(userToUpdate);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findUserByIdNative(userId);
        if (userToDelete != null ) {
            //Fjerner tilhørsforhold fra brugeren, så den efterfølgende kan slettes
            userToDelete.removeAllProjects();
            userRepository.deleteById(userId);
        }
        else throw new RuntimeException("Bruger blev ikke fundet");
    }

    @Override
    public List<User> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        if(allUsers==null){
            throw new IllegalArgumentException("Listen af brugere blev ikke fundet");
        }
        return allUsers;
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
