package com.example.demo.service;

import com.example.demo.models.Manufacturing;
import com.example.demo.repository.ManufacturingRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ManufacturingService {

    @Autowired
    ManufacturingRespository manufacturingRespository;

    public ResponseEntity<List<Manufacturing>> getAllManufacturing() {
        return new ResponseEntity<>(manufacturingRespository.findAll(), HttpStatus.OK);
    }

    public Manufacturing getManufacturingById(Long id){
        Manufacturing manufacturing = manufacturingRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturing Not Found for ID: " + id));

        return manufacturing;
    }

    public ResponseEntity<Manufacturing> addNewManufacturing(Manufacturing manufacturing) {

        return new ResponseEntity<>(manufacturingRespository.save(manufacturing),HttpStatus.OK);
    }

    public ResponseEntity<Manufacturing> updateManufacturing(Long id, Manufacturing updatedManufacturing) {
        Manufacturing manufacturing = manufacturingRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturing Not Found for ID: " + id));

        manufacturing.setManufacturing(updatedManufacturing.getManufacturing());

        manufacturingRespository.save(manufacturing);
        return new ResponseEntity<>(manufacturing,HttpStatus.OK);
    }

    public void deleteManufacturing(Long id) {
        manufacturingRespository.deleteById(id);
    }
}
