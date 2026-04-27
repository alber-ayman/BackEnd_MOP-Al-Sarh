package com.example.demo.repository;

import com.example.demo.models.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficerRepository extends JpaRepository<Officer,Long> {
    Officer findByOfficerName(String officerName);
}
