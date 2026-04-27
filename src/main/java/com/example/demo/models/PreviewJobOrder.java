package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "PreviewJobOrder")
public class PreviewJobOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    private String jobOrderId;

    private String pandCode;

    private String unifiedSerial;

    private String projectName;

    private String uniqueId;

    private String engineerName;

    private String projectCode;

    private Long projectProfileId;

    private String jobOrderType;

    private String installationArea;

    private double quantity;

    private double mainQuantity;

    private double quantityInPand;

    private String unit;

    private String manufacturing;

    private String manufacturingCode;

    private String rawType;

    private String officerName;

    private String rawUsed;

    private String finishType;

    private String thickness;

    private String height;

    private String width;

    private String repetition;

    private String total;

    private String mainTotal;

    private String description;

    private String additionalDescription;

    private String blockNumber;

    private String floor;

    private String fileDB;

    private String jobOrderTime;

    private int flag;

    private String message;

    private String quantityUsedRaws;

    private String returnReason;

}
