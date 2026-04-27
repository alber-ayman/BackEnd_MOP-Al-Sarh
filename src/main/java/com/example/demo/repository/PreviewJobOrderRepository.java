package com.example.demo.repository;

import com.example.demo.models.PandsToJobOrder;
import com.example.demo.models.PreviewJobOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreviewJobOrderRepository extends JpaRepository<PreviewJobOrder,Long> {
    List<PreviewJobOrder> getByProjectCode(String id);

    List<PreviewJobOrder> getByJobOrderId(String id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM preview_job_order where project_profile_id = :projectCode", nativeQuery = true)
    void deleteByProjectId(@Param("projectCode") Long projectCode);
}
