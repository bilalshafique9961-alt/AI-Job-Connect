package com.example.jobrecomendation;

public class Application {
    private String id;
    private String jobId;
    private String jobTitle;
    private String seekerId;
    private String seekerName;
    private String status;
    private long appliedAt;

    public Application() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getSeekerId() { return seekerId; }
    public void setSeekerId(String seekerId) { this.seekerId = seekerId; }

    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
}