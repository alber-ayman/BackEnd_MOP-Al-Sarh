/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.List;

/**
 * @author Alber_Ayman
 */
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "PROJECT_PROFILE")
public class ProjectProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @SequenceGenerator(sequenceName = "PROJECTPROFILE_SEQ", allocationSize = 1, name = "PROJECTPROFILE_SEQ")
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROJECT_CODE",unique = true)
    private String projectCode;

    private int serial;

    private String projectName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "MOBILE")
    private Long mobile;

    @Column(name = "EMAIL")
    private String email;

    private String engineerName;

    private String startDate;

    private String createdBy;

    private String createdDate;

    private String contractor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "projectProfileId")
    @JsonIgnore
    private List<Pand> pands;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "projectProfileId")
    @JsonIgnore
    private List<JobOrder> jobOrders;

}
