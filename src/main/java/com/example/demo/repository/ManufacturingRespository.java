package com.example.demo.repository;

import com.example.demo.models.Manufacturing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturingRespository extends JpaRepository<Manufacturing,Long> {
}
