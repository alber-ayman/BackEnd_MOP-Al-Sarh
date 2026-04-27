package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "files")
public class FileDB {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String type;

    private Long jobOrderId;

    private Long pandId;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;


    public FileDB(String name, String type, byte[] data,Long jobOrderId,Long pandId) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.jobOrderId = jobOrderId;
        this.pandId = pandId;
    }



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getJobOrderId() {
        return jobOrderId;
    }

    public void setJobOrderId(Long jobOrderId) {
        this.jobOrderId = jobOrderId;
    }

    public Long getPandId() {
        return pandId;
    }

    public void setPandId(Long pandId) {
        this.pandId = pandId;
    }
}
