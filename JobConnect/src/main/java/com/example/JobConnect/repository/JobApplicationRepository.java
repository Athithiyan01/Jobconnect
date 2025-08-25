package com.example.JobConnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.JobApplication;
import com.example.JobConnect.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobSeeker(User jobSeeker);
    List<JobApplication> findByJob(Job job);
    Optional<JobApplication> findByJobAndJobSeeker(Job job, User jobSeeker);
    boolean existsByJobAndJobSeeker(Job job, User jobSeeker);
}
