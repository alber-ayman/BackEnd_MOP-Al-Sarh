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
@Table(name = "returnJobOrders")
public class ReturnJobOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    private String returnReason;

    private String jobOrderId;

    private String pandCode;

    private String projectName;

    private String projectCode;

    private Long projectProfileId;

    private double quantity;

    private String unit;

    private String total;

    private String thickness;

    private String height;

    private String width;

    private String rawType;
}
