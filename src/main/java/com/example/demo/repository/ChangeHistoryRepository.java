package com.example.demo.repository;

import com.example.demo.models.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory,String> {
}
