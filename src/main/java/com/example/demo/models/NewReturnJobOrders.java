package com.example.demo.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "newReturnJobOrders")
public class NewReturnJobOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    private String pandCode;

    private String projectName;

    private String projectCode;

    private Long projectProfileId;

    private double quantity = 0;

    private String unit;

    private double total = 0;

    private String height;

    private String width;
}
