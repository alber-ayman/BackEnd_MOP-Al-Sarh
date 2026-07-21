package com.example.demo.repository;

import com.example.demo.models.Pand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface PandsRepository extends JpaRepository<Pand, Long> {

    List<Pand> findByProjectProfileId(Long Id);

    Pand findByPandCodeAndProjectProfileId(String pandCode,Long Id);

    Pand findByIdAndProjectProfileId(Long pandCode,Long Id);


    Pand findByPandCodeAndProjectCode(String pandCode,String projectCode);

    @Query(value = "SELECT raw_type FROM pand where project_profile_id = :projectCode GROUP BY raw_type", nativeQuery = true)
    List<String> getRawsPandByProjectId(@Param("projectCode") Long projectCode);

    @Query(value = "SELECT * FROM pand where project_profile_id = :projectCode and unit = :unit ", nativeQuery = true)
    List<Pand> getPandByProjectIdGroupByUnit(@Param("projectCode") Long projectCode, @Param("unit") String unit);

    @Query(value = "SELECT unit FROM pand where project_profile_id = :projectCode GROUP BY unit", nativeQuery = true)
    List<String> getAllUnits(@Param("projectCode") Long projectCode);

//    @Query(value = "select sum(rest_quantity) from pand where project_code = :projectCode and unit = :unit", nativeQuery = true)
//    Double getSumByUnit(@Param("projectCode") String projectCode, @Param("unit") String unit);

    @Query(value = "SELECT * FROM pand where project_profile_id = :projectCode and raw_type = :rawType order by thickness asc", nativeQuery = true)
    List<Pand> getPandByRawType(@Param("projectCode") Long projectCode, @Param("rawType") String rawType);

    @Query("SELECT p.restQuantity FROM Pand p WHERE p.pandCode = :pandCode AND p.projectProfileId = :projectProfileId")
    double findRestQuantityByPandCodeAndProjectProfileId(@Param("pandCode") String pandCode,
                                                         @Param("projectProfileId") Long projectProfileId);
    void deleteByProjectProfileId(Long id);
}
