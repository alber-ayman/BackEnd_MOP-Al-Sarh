package com.example.demo.controllers;

import com.example.demo.models.NewReturnJobOrders;
import com.example.demo.repository.NewReturnRepository;
import com.example.demo.service.NewReturnService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/v2/return")
public class NewReturnController {

    @Autowired
    NewReturnService newReturnService;

    @GetMapping("/byProject/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<NewReturnJobOrders>> getReturnsById(@PathVariable(name = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            List<NewReturnJobOrders> returnJobOrders = newReturnService.getReturnsByProjectId(id);

            return new ResponseEntity<>(returnJobOrders, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewReturnJobOrders> saveJobOrders(
            @RequestBody NewReturnJobOrders returnJobOrders, HttpServletRequest request) throws SQLException {
        try {
            return new ResponseEntity<>(newReturnService.addNew(returnJobOrders,request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(returnJobOrders, HttpStatus.BAD_REQUEST);
        }
    }
}
