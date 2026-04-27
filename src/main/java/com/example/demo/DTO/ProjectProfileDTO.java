package com.example.demo.DTO;

import com.example.demo.models.JobOrder;
import com.example.demo.models.Pand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


public record ProjectProfileDTO(
        Long id, String projectCode, String projectName
) {}
