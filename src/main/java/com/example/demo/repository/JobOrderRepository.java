package com.example.demo.repository;

import com.example.demo.models.JobOrder;
import com.example.demo.models.PandsToJobOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOrderRepository extends JpaRepository<JobOrder,Long> {

    @Query(value = "SELECT MAX(j.number) FROM Job_Order j where j.year = ( SELECT YEAR(NOW()) AS CurrentYear) and project_profile_id = :project_profile_id", nativeQuery = true)
    Integer findMaxNumber(@Param("project_profile_id") Long id);

    @Query(value = "SELECT MAX(j.id) FROM Job_Order j", nativeQuery = true)
    Long findMaxId();

    @Query(value = "SELECT * FROM Job_Order where project_code = :projectCode ORDER BY id DESC LIMIT 1 ", nativeQuery = true)
    JobOrder findLastInserted(@Param("projectCode") String projectCode);

    List<JobOrder> findByProjectProfileIdOrderByIdDesc(Long id);

    JobOrder getByJobOrderNumber(String jobOrder);

    void deleteByProjectProfileId(Long id);

//    @Query(value = "SELECT * FROM job_order where created_by = :userName order by id desc limit 1", nativeQuery = true)
//    JobOrder getJobOrderByUser(@Param("userName") String userName);

    @Query(value = "SELECT * FROM job_order where project_code = :code order by id desc limit 1", nativeQuery = true)
    JobOrder getJobOrderByProjectProfileId(@Param("code") String code);

    @Query(value = "SELECT * FROM job_order where job_order_number = :code and project_profile_id = :projectId", nativeQuery = true)
    JobOrder getJobOrderByProjectProfileIdAndJobOrderNumber(@Param("code") String code, @Param("projectId") Long id);

    @Query(value = """
            SELECT *
            FROM job_order
            WHERE STR_TO_DATE(job_order_date, '%d.%m.%y')
            BETWEEN STR_TO_DATE(:fromDate, '%d.%m.%y')
            AND STR_TO_DATE(:toDate, '%d.%m.%y')
            AND (:projectName IS NULL OR project_name = :projectName)
            """, nativeQuery = true)
    List<JobOrder> findFiltered(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("projectName") String projectName
    );

}
