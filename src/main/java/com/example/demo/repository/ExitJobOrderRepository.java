package com.example.demo.repository;

import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.ExitProcessJobOrder;
import com.example.demo.models.PandsToJobOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExitJobOrderRepository extends JpaRepository<ExitJobOrder, Long> {

    List<ExitJobOrder> getByJobOrderId(String id);

    @Query(value = "SELECT * FROM Exit_Job_Order where project_code = :projectCode and job_order_id = :job_order_id and serial_number = :serial GROUP BY unit", nativeQuery = true)
    List<ExitJobOrder> jobOrdersByRawType(@Param("projectCode")String projectCode, @Param("job_order_id")String jobOrderCode,@Param("serial")String serial);

    @Query(value = "SELECT * FROM Exit_Job_Order where project_code = :projectCode and job_order_id = :job_order_id and unit = :unit and serial_number = :serial order by thickness desc", nativeQuery = true)
    List<ExitJobOrder> jobOrdersByUnit(@Param("projectCode")String projectCode,@Param("job_order_id")String jobOrderCode, @Param("unit")String unit,@Param("serial")String serial);


    @Query(value = "SELECT * FROM exit_job_order where job_order_id = :jobOrderId group by serial_number", nativeQuery = true)
    List<ExitJobOrder> getByJobOrderIdAndSerial(@Param("jobOrderId") String jobOrderId);

    @Query(value = "select serial_number from exit_job_order ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastSerialNumber();

    @Query(value = "SELECT * FROM exit_job_order where serial_number = :serial", nativeQuery = true)
    List<ExitJobOrder> getBySerial(@Param("serial")String serial);

    @Query(value = "select sum(total) from exit_job_order where project_code = :projectCode and pand_code = :pandCode and return_flag != 1", nativeQuery = true)
    Double getSumByPandCode(@Param("projectCode") String projectCode, @Param("pandCode") String pandCode);

    @Query(value = "select sum(total) from exit_job_order where project_code = :projectCode and pand_code = :pandCode and raw_type = :rawType and thickness = :thickness and return_flag != 1", nativeQuery = true)
    Double getSumByRawTypeAndThinckness(@Param("projectCode") String projectCode, @Param("pandCode") String pandCode, @Param("rawType") String rawType, @Param("thickness") String thickness);

    @Query(value = "select sum(total) from exit_job_order where project_code = :projectCode and job_order_id = :jobOrderId and raw_type = :rawType and return_flag != 1", nativeQuery = true)
    Double getSumByRawType(@Param("projectCode") String projectCode, @Param("jobOrderId") String pandCode, @Param("rawType") String rawType);

    @Query(value = "select sum(quantity) from exit_job_order where project_code = :projectCode and raw_type = :rawType and return_flag != 1", nativeQuery = true)
    Double getSumByRawType(@Param("projectCode") String projectCode, @Param("rawType") String rawType);

    @Query(value = "select sum(quantity) from exit_job_order where project_code = :projectCode and job_order_id = :jobOrderId and raw_type = :rawType and return_flag != 1", nativeQuery = true)
    Double sumQuantityByRawType(@Param("projectCode") String projectCode,@Param("jobOrderId") String pandCode, @Param("rawType") String rawType);

    @Query(value = "select sum(quantity_used_raws) from exit_job_order where project_code = :projectCode and raw_type = :rawType and return_flag != 1", nativeQuery = true)
    Double getSumByQuantityUsedRaws(@Param("projectCode") String projectCode, @Param("rawType") String rawType);

    @Query(value = "select * from exit_job_order where project_profile_id = :projectCode and return_flag != 1 group by exit_job_order.raw_type ", nativeQuery = true)
    List<ExitJobOrder> getByRawType(@Param("projectCode") Long projectCode);

    @Query(value = "select * from exit_job_order where project_profile_id = :projectCode and raw_type = :rawType and return_flag != 1 ", nativeQuery = true)
    List<ExitJobOrder> getAllByRawType(@Param("projectCode") Long projectCode, @Param("rawType") String rawType);

    List<ExitJobOrder> getByUniqueId(String id);

    void deleteByProjectProfileId(Long id);

    @Query(value = "select sum(total) from exit_job_order where project_code = :projectCode and pand_code = :pandCode and job_order_id = :job_order_id and return_flag != 1", nativeQuery = true)
    Double getSumByJobOrderAndPand(@Param("projectCode") String projectCode, @Param("pandCode") String pandCode, @Param("job_order_id") String jobOrderId);

    List<ExitJobOrder> getByUnifiedSerial(String unifiedSerial);

    void deleteByUnifiedSerial(String unifiedSerial);

    @Query(value = "select * from exit_job_order where project_code = :projectCode and pand_code = :pandCode", nativeQuery = true)
    List<ExitJobOrder> getByProjectAndPandCode(@Param("projectCode") String projectCode, @Param("pandCode") String pandCode);

}
