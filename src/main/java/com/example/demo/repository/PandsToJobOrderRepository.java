package com.example.demo.repository;

import com.example.demo.models.PandsToJobOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PandsToJobOrderRepository extends JpaRepository<PandsToJobOrder, Long> {
    List<PandsToJobOrder> getByJobOrderId(String id);

    @Query(value = "SELECT unit FROM Pands_To_Job_Order where job_order_id = :job_order_id GROUP BY unit", nativeQuery = true)
    List<String> getAllUnits(@Param("job_order_id") String jobOrderCode);

    @Query(value = "SELECT * FROM Pands_To_Job_Order where unit = :unit and job_order_id = :job_order_id ", nativeQuery = true)
    List<PandsToJobOrder> getPandByProjectIdGroupByUnit(@Param("unit") String unit, @Param("job_order_id") String jobOrderCode);

    @Query(value = "SELECT job_order_id FROM Pands_To_Job_Order where project_profile_id = :projectCode GROUP BY job_order_id ", nativeQuery = true)
    List<String> jobOrdersId(@Param("projectCode") Long projectCode);

    @Query(value = "SELECT * FROM Pands_To_Job_Order where project_profile_id = :projectCode and job_order_id = :job_order_id ", nativeQuery = true)
    List<PandsToJobOrder> jobOrdersByJobOrderId(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderCode);

    @Query(value = "SELECT * FROM Pands_To_Job_Order where project_profile_id = :projectCode and pand_code = :pand_code ", nativeQuery = true)
    List<PandsToJobOrder> jobOrdersByPandCode(@Param("projectCode") Long projectCode, @Param("pand_code") String pandCode);

    List<PandsToJobOrder> getByProjectProfileId(Long id);

    @Query(value = "select sum(main_total) from pands_to_job_order where project_profile_id = :projectCode and pand_code = :pandCode", nativeQuery = true)
    Double getSumByPandCode(@Param("projectCode") Long projectCode, @Param("pandCode") String pandCode);

    @Query(value = "select sum(main_quantity * repetition) from pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id and raw_type = :raw_type", nativeQuery = true)
    Double sumQuantityByRaw(@Param("projectCode") Long projectCode, @Param("job_order_id") String pandCode, @Param("raw_type") String rawType);

    @Query(value = "SELECT * FROM pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id  group by raw_type", nativeQuery = true)
    List<PandsToJobOrder> getByJobOrderIdGroupByRawType(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderCode);

    @Query(value = "SELECT * FROM pands_to_job_order where job_order_id = :job_order_id  group by raw_type", nativeQuery = true)
    List<PandsToJobOrder> allJobOrderIdGroupByRawType(@Param("job_order_id") String jobOrderCode);
    @Query(value = "SELECT * FROM pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id and raw_type = :raw_type", nativeQuery = true)
    List<PandsToJobOrder> getByJobOrderIdAndRawType(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderCode, @Param("raw_type") String rawType);


    @Query(value = "select sum(main_total) from pands_to_job_order where project_profile_id = :projectCode and pand_code = :pandCode and raw_type = :raw_type and thickness = :thickness", nativeQuery = true)
    Double getSumByRawTypeAndThinckness(@Param("projectCode") Long projectCode, @Param("pandCode") String pandCode, @Param("raw_type") String rawType, @Param("thickness") String thickness);

    @Query(value = "select sum(main_total) from pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id and raw_type = :raw_type", nativeQuery = true)
    Double getSumByRawType(@Param("projectCode") Long projectCode, @Param("job_order_id") String pandCode, @Param("raw_type") String rawType);

    PandsToJobOrder findByJobOrderIdAndPandCode(String jobOrderid, String pandId);

    @Query(value = "select * from pands_to_job_order where job_order_id = :job_order_id and pand_code = :pandCode and height = :height and width = :width", nativeQuery = true)
    PandsToJobOrder findAllByJobOrderIdAndPandCode(@Param("job_order_id") String jobOrderCode, @Param("pandCode") String pandCode,@Param("height") String height,@Param("width") String width);
//    List<PandsToJobOrder> findByPandCode(String pandId);

    List<PandsToJobOrder> findByPandCodeAndProjectCode(String pandId,String projectCode);

    @Query(value = "select sum(quantity) from pands_to_job_order where project_profile_id = :projectCode and raw_type = :rawType", nativeQuery = true)
    Double getSumByRawType(@Param("projectCode") Long projectCode, @Param("rawType") String rawType);

    PandsToJobOrder getByUniqueId(String id);

    PandsToJobOrder getByUniqueIdAndJobOrderId(String id,String jobOrderId);

    @Query(value = "SELECT * FROM pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id and raw_type = :rawType group by thickness, unit order by thickness asc", nativeQuery = true)
    List<PandsToJobOrder> getByThicknessAndRawType(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderCode, @Param("rawType") String rawType);

    @Query(value = "SELECT * FROM pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id and raw_type = :rawType and thickness = :thickness and unit = :unit", nativeQuery = true)
    List<PandsToJobOrder> getByThicknessAndRawTypeAndUnit(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderCode, @Param("rawType") String rawType,@Param("thickness") String thickness,@Param("unit") String unit);

    @Query(value = "SELECT  * FROM mop.pands_to_job_order where project_profile_id = :projectCode and pand_code = :pandCode group by job_order_id" , nativeQuery = true)
    List<PandsToJobOrder> getPandDetails(@Param("projectCode") Long projectCode, @Param("pandCode") String pandCode);

    @Query(value = "select sum(main_quantity) from pands_to_job_order where project_profile_id = :projectCode and pand_code = :pandCode and job_order_id = :job_order_id ", nativeQuery = true)
    Double getSumByPandCodeAndJobOrder(@Param("projectCode") Long projectCode, @Param("pandCode") String pandCode , @Param("job_order_id") String job_order_id);

    @Query(value = "SELECT  * FROM mop.pands_to_job_order where project_profile_id = :projectCode and job_order_id = :job_order_id group by pand_code" , nativeQuery = true)
    List<PandsToJobOrder> getJobOrderDetails(@Param("projectCode") Long projectCode, @Param("job_order_id") String jobOrderID);


    @Transactional
    @Modifying
    @Query(value = "delete from job_order_pands_to_job_orders where pands_to_job_orders_id = :id ", nativeQuery = true)
    void deleteFromMutuleTable(@Param("id") Long id);



    void deleteByProjectProfileId(Long id);

    @Procedure(procedureName = "deducting_quantity_from_pands")
    void deductingQuantityFromPands(
            @Param("p_project_code") String projectCode,
            @Param("p_job_order_id") String jobOrderId
    );
}
