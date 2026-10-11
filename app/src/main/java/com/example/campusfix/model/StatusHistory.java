package com.example.campusfix.model;

import java.io.Serializable;

public class StatusHistory implements Serializable {
    private long id;
    private String reportId;
    private String status;
    private String notes;
    private long timestamp;
    private String updatedBy;

    public StatusHistory() {
    }

    public StatusHistory(long id, String reportId, String status, String notes, long timestamp, String updatedBy) {
        this.id = id;
        this.reportId = reportId;
        this.status = status;
        this.notes = notes;
        this.timestamp = timestamp;
        this.updatedBy = updatedBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
