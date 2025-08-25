package com.example.JobConnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id")
    private User jobSeeker;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    private LocalDateTime appliedAt = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    
    // Constructors
    public JobApplication() {}
    
    public JobApplication(Job job, User jobSeeker, String coverLetter) {
        this.job = job;
        this.jobSeeker = jobSeeker;
        this.coverLetter = coverLetter;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    
    public User getJobSeeker() { return jobSeeker; }
    public void setJobSeeker(User jobSeeker) { this.jobSeeker = jobSeeker; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
}
