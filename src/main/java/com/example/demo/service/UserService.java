/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.service;

import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.login.RoleRepository;
import com.example.demo.repository.login.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Alber_Ayman
 */
@Service
public class UserService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findAllById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public ResponseEntity<User> updateUser(Long id, User user) {
        User user1 = getUserById(id);
        user1.setUsername(user.getUsername());
        user1.setPassword(user1.getPassword());
        user1.setEmail(user.getEmail());
//        user1.setRole(user.getRole());

        String role = "";

//        String strRoles = user.getRole();
//        Set<Role> roles = new HashSet<>();
//
//        System.out.println("//////" + strRoles + "/////////");
//
//        switch (strRoles) {
//            case "ADMIN":
//                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                roles.add(adminRole);
//                role = "ADMIN";
//                break;
//
//            case "MODERATOR":
//                Role modRole = roleRepository.findByName(ERole.ROLE_VIEWER)
//                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                roles.add(modRole);
//                role = "MODERATOR";
//                break;
//            default:
//                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                roles.add(userRole);
//                role = "User";
//        }
//
        user1.setRole("ADMIN");
//        user1.setRoles(roles);

        user1.setViewProject(user.isViewProject());
        user1.setAddProject(user.isAddProject());
        user1.setEditProject(user.isEditProject());
        user1.setDeleteProject(user.isDeleteProject());

        user1.setViewPand(user.isViewPand());
        user1.setAddPand(user.isAddPand());
        user1.setEditPand(user.isEditPand());
        user1.setDeletePand(user.isDeletePand());

        user1.setViewJobOrder(user.isViewJobOrder());
        user1.setAddJobOrder(user.isAddJobOrder());
        user1.setEditJobOrder(user.isEditJobOrder());
        user1.setDeleteJobOrder(user.isDeleteJobOrder());
        user1.setReturnJobOrder(user.isReturnJobOrder());
        user1.setViewExitJobOrder(user.isViewExitJobOrder());
        user1.setAddExitJobOrder(user.isAddExitJobOrder());
        user1.setEditExitJobOrder(user.isEditExitJobOrder());
        user1.setDeleteExitJobOrder(user.isDeleteExitJobOrder());


        user1.setShowReports(user.isShowReports());
        user1.setRecordDelivered(user.isRecordDelivered());
        user1.setAllAuth(user.isAllAuth());
        user1.setViewOnly(user.isViewOnly());
        user1.setEditUser(user.isEditUser());
        userRepository.save(user1);

        return new ResponseEntity<>(user1, HttpStatus.OK);
    }
}
