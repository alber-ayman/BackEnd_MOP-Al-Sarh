package com.example.demo.service;

import com.example.demo.models.Engineers;
import com.example.demo.models.JobOrder;
import com.example.demo.models.Officer;
import com.example.demo.repository.EngineersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class EngineersService {

    @Autowired
    EngineersRepository engineersRepository;

    public ResponseEntity<List<Engineers>> getAllEngineers() {
        return new ResponseEntity<>(engineersRepository.findAll(), HttpStatus.OK);
    }

    public Engineers getEngineerById(Long id){
        Engineers engineers = engineersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Engineers Not Found for ID: " + id));

        return engineers;
    }

    public ResponseEntity<Engineers> addNewEngineer(Engineers engineer) {
        try {
            Engineers engineers = engineersRepository.findByEngineerName(engineer.getEngineerName());
            if (engineers == null) {
                return new ResponseEntity<>(engineersRepository.save(engineer), HttpStatus.OK);
            } else {
                Engineers engineers1 = new Engineers();
                engineers1.setFlagId(1);
                engineers1.setMessage("المهندس موجود بالفعل");
                return new ResponseEntity<>(engineers1, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<Engineers> updateEngineers(Long id, Engineers updatedEngineers) {
        Engineers engineers = engineersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        engineers.setEngineerName(updatedEngineers.getEngineerName());

        engineersRepository.save(engineers);
        return new ResponseEntity<>(engineers,HttpStatus.OK);
    }

    public void deleteEngineer(Long id) {
        engineersRepository.deleteById(id);
    }
}
