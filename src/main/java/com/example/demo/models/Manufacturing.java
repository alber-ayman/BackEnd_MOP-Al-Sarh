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
@Table(name = "Manufacturing")
public class Manufacturing {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String manufacturing;

    private Long projectProfileId;
}
