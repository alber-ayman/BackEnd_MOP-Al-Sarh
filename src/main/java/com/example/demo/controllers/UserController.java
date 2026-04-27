/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.controllers;


import java.sql.SQLException;
import java.util.List;

import com.example.demo.models.JobOrder;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Alber_Ayman
 */
//@CrossOrigin(origins = "http://192.168.196.44:80" , maxAge = 3600)
//@CrossOrigin(allowCredentials = "true")
@CrossOrigin(origins = "http://192.168.1.249:4200")
@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

//    @Autowired
//    MsgLogging msgLogging;
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() throws SQLException {
        try {
            List<User> allUsers = userService.getAllUsers();

            if (allUsers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } catch (Exception e) {
            e.getStackTrace();

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long id) throws SQLException {
        try {
            User allUsers = userService.getUserById(id);

            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } catch (Exception e) {
            e.getStackTrace();

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateJobOrders(@PathVariable(value = "id") Long id, @RequestBody User user) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("111111111111111111");
            return userService.updateUser(id, user);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }
}
