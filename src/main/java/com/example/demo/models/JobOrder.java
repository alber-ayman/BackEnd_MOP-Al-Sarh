package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "Job_Order")
public class JobOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(sequenceName = "PAND_SEQ", allocationSize = 1, name = "PAND_SEQ")
    @Column(name = "ID")
    private Long id;

    private String jobOrderNumber;

    private String projectName;

    private Integer  number;

    private String projectCode;

    private String jobOrderDate;

    private int year;

    private String jobOrderTime;

    private Long projectProfileId;

    private String fileDB;

    private String fileId;

    private String createdBy;

    private String installementArea;

    private boolean commit;

    private boolean approved;

    private int flag;

    private String message;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PandsToJobOrder> pandsToJobOrders;
}
