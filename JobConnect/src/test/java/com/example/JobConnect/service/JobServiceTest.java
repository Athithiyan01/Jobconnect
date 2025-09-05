package com.example.JobConnect.service;

import com.example.JobConnect.dto.JobDto;
import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateJob() {
        JobDto jobDto = new JobDto();
        jobDto.setTitle("Software Engineer");
        jobDto.setDescription("Develop applications");
        jobDto.setLocation("Remote");
        jobDto.setSalary(100000.0);
        jobDto.setDeadline(LocalDateTime.now().plusDays(30));

        User employer = new User();
        employer.setId(1L);
        employer.setFullName("John Doe");
        employer.setPhoneNumber("1234567890");

        Job savedJob = new Job();
        savedJob.setTitle("Software Engineer");
        savedJob.setDescription("Develop applications");

        when(jobRepository.save(any(Job.class))).thenReturn(savedJob);

        Job result = jobService.createJob(jobDto, employer);

        assertNotNull(result);
        assertEquals("Software Engineer", result.getTitle());
        verify(jobRepository, times(1)).save(any(Job.class));
        verify(smsService, times(1))
                .sendSms(eq("1234567890"), contains("Software Engineer"));
    }

    @Test
    void testFindById() {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Optional<Job> result = jobService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Backend Developer", result.get().getTitle());
    }

    @Test
    void testGetAllJobs() {
        Job job1 = new Job(); job1.setTitle("DevOps Engineer");
        Job job2 = new Job(); job2.setTitle("Frontend Developer");

        when(jobRepository.findByOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(job1, job2));

        List<Job> jobs = jobService.getAllJobs();

        assertEquals(2, jobs.size());
        assertEquals("DevOps Engineer", jobs.get(0).getTitle());
    }
}
