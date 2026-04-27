package com.example.demo.repository;

import com.example.demo.models.Discription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscriptionRepository extends JpaRepository<Discription,Long> {
}
