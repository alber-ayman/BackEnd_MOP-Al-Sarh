package com.example.demo.service;

import com.example.demo.models.FinishTypes;
import com.example.demo.models.Officer;
import com.example.demo.repository.FinishTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinishTypesService {

    @Autowired
    FinishTypesRepository finishTypesRepository;

    public ResponseEntity<List<FinishTypes>> getAllFinishTypes() {
        return new ResponseEntity<>(finishTypesRepository.findAll(), HttpStatus.OK);
    }

    public FinishTypes getFinishTypesById(Long id){
        FinishTypes finishTypes = finishTypesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("officer Not Found for ID: " + id));

        return finishTypes;
    }

    public ResponseEntity<FinishTypes> addNewFinishTypes(FinishTypes finishTypes) {

        return new ResponseEntity<>(finishTypesRepository.save(finishTypes),HttpStatus.OK);
    }

    public ResponseEntity<FinishTypes> updateFinishTypes(Long id, FinishTypes updatedFinishTypes) {
        FinishTypes finishTypes = finishTypesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Officer Not Found for ID: " + id));

        finishTypes.setFinishType(updatedFinishTypes.getFinishType());

        finishTypesRepository.save(finishTypes);
        return new ResponseEntity<>(finishTypes,HttpStatus.OK);
    }

    public void deleteFinishTypes(Long id) {
        finishTypesRepository.deleteById(id);
    }
}
