package com.example.demo.repository;

import com.example.demo.DTO.ProjectProfileDTO;
import com.example.demo.models.ProjectProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectProfileRepository extends JpaRepository<ProjectProfile,Long> {
    ProjectProfile getById(Long projectCode);

    @Query(value = "SELECT id,project_Code,project_Name FROM project_profile p ", nativeQuery = true)
    List<ProjectProfileDTO> getAllProjectProfiles();
}
