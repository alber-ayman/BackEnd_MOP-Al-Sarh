package com.example.demo.repository;

import com.example.demo.models.PandsToJobOrder;
import com.example.demo.models.ReturnJobOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnJobOrders, Long> {
    @Query(value = "SELECT * FROM Return_Job_Orders where job_order_id = :job_order_id ", nativeQuery = true)
    List<ReturnJobOrders> getReturnsById(@Param("job_order_id") String jobOrderCode);

}
