package com.example.demo.repository;

import com.example.demo.models.NewReturnJobOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewReturnRepository extends JpaRepository<NewReturnJobOrders, Long> {

    NewReturnJobOrders findByPandCodeAndProjectCode(String pandCode, String projectCode);

    List<NewReturnJobOrders> findByProjectProfileId(Long id);
}
