package com.example.demo.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "ChangeHistory")
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    private String logId;

    private LocalDateTime logDate;

    private String modifiedBy;

    @Lob
    @Column(name = "message_Request")
    private String messageRequest;

    @Lob
    @Column(name = "message_Response")
    private String messageResponse;

    private String action;
}
