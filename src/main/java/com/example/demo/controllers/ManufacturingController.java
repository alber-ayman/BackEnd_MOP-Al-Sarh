package com.example.demo.controllers;

import com.example.demo.models.Manufacturing;
import com.example.demo.service.ManufacturingService;
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
@RequestMapping("/api/Manufacturing")
public class ManufacturingController {

    @Autowired
    ManufacturingService manufacturingService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Manufacturing>> getAllManufacturing() throws SQLException {
        try {
            return manufacturingService.getAllManufacturing();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Manufacturing> getManufacturingById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            Manufacturing manufacturing = manufacturingService.getManufacturingById(id);

            return new ResponseEntity<>(manufacturing, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Manufacturing> saveManufacturing(
            @RequestBody Manufacturing manufacturing) throws SQLException {
        try {
            return manufacturingService.addNewManufacturing(manufacturing);
        } catch (Exception e) {
            return new ResponseEntity<>(manufacturing, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Manufacturing> updateManufacturing(@PathVariable(value = "id") Long id, @RequestBody Manufacturing manufacturing) throws ResourceNotFoundException, SQLException {
        try {
            return manufacturingService.updateManufacturing(id, manufacturing);
        } catch (Exception e) {
            return new ResponseEntity<>(manufacturing, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteManufacturing(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            manufacturingService.deleteManufacturing(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
