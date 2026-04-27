package com.example.demo.repository;

import com.example.demo.models.Engineers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineersRepository extends JpaRepository<Engineers,Long> {
    Engineers findByEngineerName(String engineerName);
}
