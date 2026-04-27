package com.example.demo.controllers;

import com.example.demo.models.Discription;
import com.example.demo.models.Engineers;
import com.example.demo.service.DiscriptionService;
import com.example.demo.service.EngineersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://192.168.1.249:4200")
@RequestMapping("/api/Discription")
public class DiscriptionController {

    @Autowired
    DiscriptionService discriptionService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Discription>> getAllDiscription() throws SQLException {
        try {
            return discriptionService.getAllDiscription();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Discription> getDiscriptionvById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            Discription discription = discriptionService.getDiscriptionById(id);

            return new ResponseEntity<>(discription, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Discription> saveDiscription(
            @RequestBody Discription discription) throws SQLException {
        try {
            return discriptionService.addNewDiscription(discription);
        } catch (Exception e) {
            return new ResponseEntity<>(discription, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Discription> updateDiscription(@PathVariable(value = "id") Long id, @RequestBody Discription discription) throws ResourceNotFoundException, SQLException {
        try {
            return discriptionService.updateDiscription(id,discription);
        } catch (Exception e) {
            return new ResponseEntity<>(discription, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDiscription(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            discriptionService.deleteDiscription(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
