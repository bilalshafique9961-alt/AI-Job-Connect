package com.example.jobrecomendation;

import java.util.List;

public class Job {
    private String id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String experience;
    private List<String> skills;
    private String description;
    private List<String> questions;
    private String employerId;
    private long createdAt;
    private int applicantsCount;
    private int matchScore;  // ← NEW FIELD

    public Job() {}

    // Existing getters/setters...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }

    public String getEmployerId() { return employerId; }
    public void setEmployerId(String employerId) { this.employerId = employerId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getApplicantsCount() { return applicantsCount; }
    public void setApplicantsCount(int applicantsCount) { this.applicantsCount = applicantsCount; }

    public int getMatchScore() { return matchScore; }  // ← NEW
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }  // ← NEW
}