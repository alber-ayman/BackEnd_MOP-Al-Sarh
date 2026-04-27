package com.example.demo.controllers;

import com.example.demo.models.FinishTypes;
import com.example.demo.service.FinishTypesService;
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
@RequestMapping("/api/FinishTypes")
public class FinishTypesController {

    @Autowired
    FinishTypesService finishTypesService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<FinishTypes>> getAllFinishTypes() throws SQLException {
        try {
            return finishTypesService.getAllFinishTypes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<FinishTypes> getFinishTypesById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            FinishTypes finishTypes = finishTypesService.getFinishTypesById(id);

            return new ResponseEntity<>(finishTypes, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinishTypes> saveFinishTypes(
            @RequestBody FinishTypes finishTypes) throws SQLException {
        try {
            return finishTypesService.addNewFinishTypes(finishTypes);
        } catch (Exception e) {
            return new ResponseEntity<>(finishTypes, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinishTypes> updatefinishTypes(@PathVariable(value = "id") Long id, @RequestBody FinishTypes finishTypes) throws ResourceNotFoundException, SQLException {
        try {
            return finishTypesService.updateFinishTypes(id, finishTypes);
        } catch (Exception e) {
            return new ResponseEntity<>(finishTypes, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFinishTypes(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            finishTypesService.deleteFinishTypes(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
