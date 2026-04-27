package com.example.demo.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "PAND")
public class Pand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(sequenceName = "PAND_SEQ", allocationSize = 1, name = "PAND_SEQ")
    @Column(name = "ID")
    private Long id;

    private String pandCode;

    private String projectCode;

    private String projectName;

    private String engineerName;

    private Long projectProfileId;

    private String description;

    private String additionalDescription;

    @PositiveOrZero
    private double restQuantity;

    private double mainQuantity;

    private String unit;

    private String updatedDate;

    private String manufacturing;

    private String rawType;

    private String rawUsed;

    private String finishType;

    private String thickness;

    private String height;

    private String width;

    private String repetition;

    private double total;

    private String fileDB;

    private String fileId;

    private int flag;

    private String message;

    private double additionalQuantity ;

    private double totalQuantity ;

    private String additionalQuantityDate ;

    private String additionalReason ;

    private String additionalBy ;

    private String imageDescription;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

}
