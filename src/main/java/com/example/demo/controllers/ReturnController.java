package com.example.demo.controllers;

import com.example.demo.models.Engineers;
import com.example.demo.models.ReturnJobOrders;
import com.example.demo.repository.ReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/return")
public class ReturnController {

    @Autowired
    ReturnRepository returnRepository;

    @GetMapping("/byJobOrder")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<ReturnJobOrders>> getReturnsById(@RequestParam(name = "jobOrderNumber") String jobOrderNumber) throws ResourceNotFoundException, SQLException {
        try {
            List<ReturnJobOrders> returnJobOrders = returnRepository.getReturnsById(jobOrderNumber);

            return new ResponseEntity<>(returnJobOrders, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReturnJobOrders> saveJobOrders(
            @RequestBody ReturnJobOrders returnJobOrders) throws SQLException {
        try {
            return new ResponseEntity<>(returnRepository.save(returnJobOrders), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(returnJobOrders, HttpStatus.BAD_REQUEST);
        }
    }
}
