package com.example.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueryRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public QueryRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String sql = "SET global sql_mode = (SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''))";

        // Use update() for statements that do not return a result set
        jdbcTemplate.update(sql);

        System.out.println("SQL mode updated successfully!");
    }
}
