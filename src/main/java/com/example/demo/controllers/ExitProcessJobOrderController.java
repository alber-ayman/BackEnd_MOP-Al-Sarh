package com.example.demo.controllers;

import com.example.demo.models.JobOrderParent;
import com.example.demo.models.UnifiedSerial;
import com.example.demo.service.ExitProcessJobOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/exitProcessJobOrder")
public class ExitProcessJobOrderController {

    @Autowired
    ExitProcessJobOrderService jobOrderService;

    @PostMapping("/save/{serial}")  // Creating Project profile
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrderParent> saveChildPand(
            @PathVariable("serial") String unifiedSerial,
            @RequestBody JobOrderParent jobOrderParent
            ) throws SQLException {
        try {
            return jobOrderService.saveExitProcessJobOrder(jobOrderParent,unifiedSerial);
        } catch (Exception e) {
            return new ResponseEntity<>(jobOrderParent, HttpStatus.BAD_REQUEST);
        }
    }
}
