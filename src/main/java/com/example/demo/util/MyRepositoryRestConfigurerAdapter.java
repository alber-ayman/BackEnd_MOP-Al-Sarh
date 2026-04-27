/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.util;

import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
//import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

//@Configuration
//public class MyRepositoryRestConfigurerAdapter extends RepositoryRestConfigurerAdapter {

//    @Autowired
//    private EntityManager entityManager;
//
//    @Override
//    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(e -> e.getJavaType()).collect(Collectors.toList()).toArray(new Class[0]));
//    }

//}