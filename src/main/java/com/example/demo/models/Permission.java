/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;


/**
 *
 * @author Alber_Ayman
 */

@Entity
@Table(name = "PERMISSION")
@Data
@NoArgsConstructor
@Getter
@Setter
public class Permission {
    
    @Id
    // using Mysql @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(sequenceName = "PERMISSIONS_SEQ", allocationSize = 1, name = "PERMISSIONS_SEQ")
    private Long id;
    
    @Column(name = "PERMISION_NAME")
    private String permissionName;

    public Permission(String permissionName) {
        this.permissionName = permissionName;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
    
    
}
