package com.example.demo.service;

import com.example.demo.models.Discription;
import com.example.demo.models.Engineers;
import com.example.demo.repository.DiscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscriptionService {

    @Autowired
    DiscriptionRepository discriptionRepository;


    public ResponseEntity<List<Discription>> getAllDiscription() {
        return new ResponseEntity<>(discriptionRepository.findAll(), HttpStatus.OK);
    }

    public Discription getDiscriptionById(Long id){
        Discription discription = discriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discription Not Found for ID: " + id));

        return discription;
    }

    public ResponseEntity<Discription> addNewDiscription(Discription discription) {

        return new ResponseEntity<>(discriptionRepository.save(discription),HttpStatus.OK);
    }

    public ResponseEntity<Discription> updateDiscription(Long id, Discription discriptionUpdated) {
        Discription discription = discriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        discription.setDiscriptionName(discriptionUpdated.getDiscriptionName());

        discriptionRepository.save(discription);
        return new ResponseEntity<>(discription,HttpStatus.OK);
    }

    public void deleteDiscription(Long id) {
        discriptionRepository.deleteById(id);
    }

}
