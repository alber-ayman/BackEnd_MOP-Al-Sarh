package com.example.demo.repository;

import com.example.demo.models.RawTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTypeRepository extends JpaRepository<RawTypes,Long> {

}
