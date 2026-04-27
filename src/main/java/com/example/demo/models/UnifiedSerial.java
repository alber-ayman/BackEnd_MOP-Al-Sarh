package com.example.demo.models;

public class UnifiedSerial {
    private String jobOrderNumber;
    private String unifiedSerial;

    public UnifiedSerial(String jobOrderNumber, String unifiedSerial) {
        this.jobOrderNumber = jobOrderNumber;
        this.unifiedSerial = unifiedSerial;
    }

    public String getJobOrderNumber() {
        return jobOrderNumber;
    }

    public void setJobOrderNumber(String jobOrderNumber) {
        this.jobOrderNumber = jobOrderNumber;
    }

    public String getUnifiedSerial() {
        return unifiedSerial;
    }

    public void setUnifiedSerial(String unifiedSerial) {
        this.unifiedSerial = unifiedSerial;
    }
}
