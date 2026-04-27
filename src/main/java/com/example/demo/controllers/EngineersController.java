package com.example.demo.controllers;

import com.example.demo.models.Engineers;
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
@RequestMapping("/api/Engineers")
public class EngineersController {

    @Autowired
    EngineersService engineersService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Engineers>> getAllEngineers() throws SQLException {
        try {
            return engineersService.getAllEngineers();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Engineers> getEngineersById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            Engineers engineers = engineersService.getEngineerById(id);

            return new ResponseEntity<>(engineers, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Engineers> saveJobOrders(
            @RequestBody Engineers engineers) throws SQLException {
        try {
            return engineersService.addNewEngineer(engineers);
        } catch (Exception e) {
            return new ResponseEntity<>(engineers, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Engineers> updateEngineers(@PathVariable(value = "id") Long id, @RequestBody Engineers engineers) throws ResourceNotFoundException, SQLException {
        try {
            return engineersService.updateEngineers(id,engineers);
        } catch (Exception e) {
            return new ResponseEntity<>(engineers, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEngineers(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            engineersService.deleteEngineer(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
