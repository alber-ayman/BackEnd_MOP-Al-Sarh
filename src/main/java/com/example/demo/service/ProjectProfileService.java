package com.example.demo.service;

import com.example.demo.DTO.ProjectProfileDTO;
import com.example.demo.models.Pand;
import com.example.demo.models.ProjectProfile;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
//@EnableCaching
public class ProjectProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectProfileService.class);
    @Autowired
    ProjectProfileRepository projectProfileRepository;

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    @Autowired
    JobOrderRepository jobOrderRepository;

    @Cacheable(value = "PROJECT_PROFILE_ALL")
    public List<ProjectProfile> getAllProjectProfiles() {
        try {
            logger.info("fetching all from the method");
            return projectProfileRepository.findAll();
////                    .map(this::toDTO)
//                    .toList();

//            return projectProfiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public ProjectProfile getProjectProfileById(Long id) {

        logger.info("fetching from the method by id: {}", id);

        ProjectProfile projectProfile = projectProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("project Not Found for ID: " + id));

        List<Pand> pands = pandsRepository.findByProjectProfileId(projectProfile.getId());

        if (!pands.isEmpty()) {
            projectProfile.setPands(pands);
        }

        return projectProfile;
    }

//    @CachePut(value = "PROJECT_PROFILE", key = "#result.id")
//    @CacheEvict(value = "PROJECT_PROFILE_ALL", allEntries = true)
    public ProjectProfile addProjectProfile(
            ProjectProfile projectProfile) throws SQLException {
        try {

            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj);
            projectProfile.setProjectCode(projectProfile.getProjectCode().trim());
            projectProfile.setCreatedDate(formattedDate);
            projectProfileRepository.save(projectProfile);

            return projectProfile;
        } catch (Exception e) {
            return null;
        }
    }

//    @CachePut(value = "PROJECT_PROFILE", key = "#id")
//    @CacheEvict(value = "PROJECT_PROFILE_ALL", allEntries = true)
    public ProjectProfile updateProjectProfile(Long id, ProjectProfile updatedProject) throws ResourceNotFoundException, SQLException {
        try {
            ProjectProfile project = projectProfileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("project Not Found for ID: " + id));

//            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            project.setUpdatedBy(userDetails.getUsername());

            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj);

            project.setProjectCode(updatedProject.getProjectCode());
            project.setProjectName(updatedProject.getProjectName());
            project.setAddress(updatedProject.getAddress());

            project.setEngineerName(updatedProject.getEngineerName());

            project.setCreatedBy(updatedProject.getCreatedBy());
            project.setCreatedDate(updatedProject.getCreatedDate());
            project.setEmail(updatedProject.getEmail());
            project.setContractor(updatedProject.getContractor());
            project.setMobile(updatedProject.getMobile());
            project.setStartDate(updatedProject.getStartDate());

            ProjectProfile saved = projectProfileRepository.save(project);
            return saved;
        } catch (Exception e) {
            return null;
        }
    }


//    @CacheEvict(value = "PROJECT_PROFILE", key = "#id")
//    @CacheEvict(value = "PROJECT_PROFILE_ALL", allEntries = true)
    @Transactional
    public String deleteProjectProfile(Long id) {
        try {

            exitJobOrderRepository.deleteByProjectProfileId(id);

            jobOrderRepository.deleteByProjectProfileId(id);

            pandsToJobOrderRepository.deleteByProjectProfileId(id);

            pandsRepository.deleteByProjectProfileId(id);

            projectProfileRepository.deleteById(id);

            return "Deleted Successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Not Deleted";

        }
    }

    private ProjectProfileDTO toDTO(ProjectProfile p) {
        return new ProjectProfileDTO(
                p.getId(),
                p.getProjectCode(),
                p.getProjectName()

        );
    }
}
