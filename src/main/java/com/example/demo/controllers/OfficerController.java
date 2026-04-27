package com.example.demo.controllers;

import com.example.demo.models.Engineers;
import com.example.demo.models.Officer;
import com.example.demo.service.EngineersService;
import com.example.demo.service.OfficerSerive;
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
@RequestMapping("/api/Officer")
public class OfficerController {

    @Autowired
    OfficerSerive officerSerive;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Officer>> getAllOfficer() throws SQLException {
        try {
            return officerSerive.getAllOfficer();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Officer> getOfficerById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            Officer officer = officerSerive.getOfficerById(id);

            return new ResponseEntity<>(officer, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Officer> saveOfficer(
            @RequestBody Officer officer) throws SQLException {
        try {
            return officerSerive.addNewOfficer(officer);
        } catch (Exception e) {
            return new ResponseEntity<>(officer, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Officer> updateOfficer(@PathVariable(value = "id") Long id, @RequestBody Officer officer) throws ResourceNotFoundException, SQLException {
        try {
            return officerSerive.updateOfficer(id,officer);
        } catch (Exception e) {
            return new ResponseEntity<>(officer, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteOfficer(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            officerSerive.deleteOfficer(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
