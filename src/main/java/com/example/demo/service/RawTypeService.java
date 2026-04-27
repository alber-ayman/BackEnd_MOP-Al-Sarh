package com.example.demo.service;

import com.example.demo.models.Pand;
import com.example.demo.models.RawTypes;
import com.example.demo.repository.RawTypeRepository;
import org.hibernate.annotations.processing.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RawTypeService {

    @Autowired
    RawTypeRepository rawTypeRepository;

    public ResponseEntity<List<RawTypes>> getAllRawTypes() {
        List<RawTypes> rawTypes = rawTypeRepository.findAll();

        List<RawTypes> distinctElements = rawTypes.stream()
                .distinct()
                .sorted((p1, p2) -> p1.getRawTypeName().compareToIgnoreCase(p2.getRawTypeName()))
                .collect(Collectors.toList());


        return new ResponseEntity<>(distinctElements, HttpStatus.OK);

    }

    public ResponseEntity<RawTypes> getRawTypeById(Long id) {
        RawTypes rawTypes = rawTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        return new ResponseEntity<>(rawTypes, HttpStatus.OK);

    }

    public ResponseEntity<RawTypes> saveRawType(RawTypes rawTypes) throws SQLException {
        try {
            UUID uuid = UUID.randomUUID();
            // Convert UUID to a string and take the first 4 hex digits
            String uuidString = uuid.toString().replace("-", ""); // Remove hyphens
            String unique4DigitId = uuidString.substring(0, 4); // Take the first 4 characters

            // Convert to integer and ensure it's within 4-digit range
            int id = Integer.parseInt(unique4DigitId, 16) % 10000; // Convert from hex to decimal and ensure it's 4 digits

            rawTypes.setRawTypeCode(String.format("%04d", id));
            rawTypeRepository.save(rawTypes);
            return new ResponseEntity<>(rawTypes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(rawTypes, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RawTypes> updateRawType(Long id, RawTypes updatedRawTypes) {
        RawTypes rawTypes = rawTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        rawTypes.setRawTypeName(updatedRawTypes.getRawTypeName());
        rawTypes.setRawTypeCode(updatedRawTypes.getRawTypeCode());
        rawTypeRepository.save(rawTypes);
        return new ResponseEntity<>(rawTypes, HttpStatus.OK);

    }

    public void deleteRawType(Long id) {
        RawTypes rawTypes = rawTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        rawTypeRepository.deleteById(id);
    }
}
