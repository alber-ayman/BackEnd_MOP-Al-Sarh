package com.example.demo.repository;

import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.ExitProcessJobOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExitProcessJobOrderRepository extends JpaRepository<ExitProcessJobOrder,Long> {
    @Query(value = "SELECT * FROM Exit_Process_Job_Order where project_code = :projectCode and job_order_id = :job_order_id GROUP BY unit", nativeQuery = true)
    List<ExitProcessJobOrder> jobOrdersByRawType(@Param("projectCode")String projectCode, @Param("job_order_id")String jobOrderCode);

    @Query(value = "SELECT * FROM Exit_Process_Job_Order where project_code = :projectCode and job_order_id = :job_order_id and unit = :unit order by thickness desc", nativeQuery = true)
    List<ExitProcessJobOrder> jobOrdersByUnit(@Param("projectCode")String projectCode,@Param("job_order_id")String jobOrderCode, @Param("unit")String unit );

    void deleteByUnifiedSerial(String unifiedSerial);

    List<ExitProcessJobOrder> getByUnifiedSerial(String unifiedSerial);
}
