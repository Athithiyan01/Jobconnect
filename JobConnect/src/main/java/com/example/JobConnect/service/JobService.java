package com.example.JobConnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.JobConnect.dto.JobDto;
import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.repository.JobRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private SmsService smsService;
    
    public Job createJob(JobDto jobDto, User employer) {
        Job job = new Job();
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setDeadline(jobDto.getDeadline());
        job.setEmployer(employer);
        
        Job savedJob = jobRepository.save(job);
        
        // Send SMS notification to employer
        if (employer.getPhoneNumber() != null && !employer.getPhoneNumber().isEmpty()) {
            smsService.sendSms(employer.getPhoneNumber(), 
                "Your job posting '" + savedJob.getTitle() + "' has been created successfully!");
        }
        
        return savedJob;
    }
    
    public List<Job> getAllJobs() {
        return jobRepository.findByOrderByCreatedAtDesc();
    }
    
    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }
    
    public List<Job> searchJobs(String keyword, String location) {
        return jobRepository.findByKeywordAndLocation(keyword, location);
    }
    
    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }
    
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
    
    public Job updateJob(Long id, JobDto jobDto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setDeadline(jobDto.getDeadline());
        
        return jobRepository.save(job);
    }
}