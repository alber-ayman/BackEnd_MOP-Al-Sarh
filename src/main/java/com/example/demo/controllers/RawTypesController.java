package com.example.demo.controllers;

import com.example.demo.models.RawTypes;
import com.example.demo.service.RawTypeService;
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
@RequestMapping("/api/rawTypes")
public class RawTypesController {

    @Autowired
    RawTypeService rawTypeService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<RawTypes>> getAllRawTypes() throws SQLException {
        try {
            return rawTypeService.getAllRawTypes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<RawTypes> getRawTypesById(@PathVariable(value = "id") Long id) throws SQLException {
        try {
            return rawTypeService.getRawTypeById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New Pand
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RawTypes> saveRawType(
            @RequestBody RawTypes rawType) throws SQLException {
        try {
            return rawTypeService.saveRawType(rawType);
        } catch (Exception e) {
            return new ResponseEntity<>(rawType, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RawTypes> updateRawType(@PathVariable(value = "id") Long id, @RequestBody RawTypes rawType) throws ResourceNotFoundException, SQLException {
        try {
            return rawTypeService.updateRawType(id,rawType);
        } catch (Exception e) {
            return new ResponseEntity<>(rawType, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRawType(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            rawTypeService.deleteRawType(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}
