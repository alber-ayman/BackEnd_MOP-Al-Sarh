package com.example.demo.repository;

import com.example.demo.models.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDBRepository extends JpaRepository <FileDB, String> {
    FileDB findByJobOrderId(Long id);

     void deleteByJobOrderId(Long id);
    FileDB findByPandId(Long id);
}
