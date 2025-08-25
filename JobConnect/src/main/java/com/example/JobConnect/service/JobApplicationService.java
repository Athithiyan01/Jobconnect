package com.example.JobConnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.JobConnect.entity.ApplicationStatus;
import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.JobApplication;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.repository.JobApplicationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private SmsService smsService;
    
    public JobApplication applyForJob(Job job, User jobSeeker, String coverLetter) {
        if (jobApplicationRepository.existsByJobAndJobSeeker(job, jobSeeker)) {
            throw new RuntimeException("You have already applied for this job");
        }
        
        JobApplication application = new JobApplication(job, jobSeeker, coverLetter);
        JobApplication savedApplication = jobApplicationRepository.save(application);
        
        // Send SMS to job seeker
        if (jobSeeker.getPhoneNumber() != null && !jobSeeker.getPhoneNumber().isEmpty()) {
            smsService.sendSms(jobSeeker.getPhoneNumber(), 
                "Your application for '" + job.getTitle() + "' has been submitted successfully!");
        }
        
        // Send SMS to employer
        if (job.getEmployer().getPhoneNumber() != null && !job.getEmployer().getPhoneNumber().isEmpty()) {
            smsService.sendSms(job.getEmployer().getPhoneNumber(), 
                "New application received for your job posting: " + job.getTitle());
        }
        
        return savedApplication;
    }
    
    public List<JobApplication> getApplicationsByJobSeeker(User jobSeeker) {
        return jobApplicationRepository.findByJobSeeker(jobSeeker);
    }
    
    public List<JobApplication> getApplicationsByJob(Job job) {
        return jobApplicationRepository.findByJob(job);
    }
    
    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        application.setStatus(status);
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        // Send SMS notification about status update
        User jobSeeker = application.getJobSeeker();
        if (jobSeeker.getPhoneNumber() != null && !jobSeeker.getPhoneNumber().isEmpty()) {
            String message = "Your application for '" + application.getJob().getTitle() + 
                           "' status has been updated to: " + status.toString();
            smsService.sendSms(jobSeeker.getPhoneNumber(), message);
        }
        
        return updatedApplication;
    }
    
    public Optional<JobApplication> findById(Long id) {
        return jobApplicationRepository.findById(id);
    }
}

