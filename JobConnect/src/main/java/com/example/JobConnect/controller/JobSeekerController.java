package com.example.JobConnect.controller;

import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.JobApplication;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.service.JobApplicationService;
import com.example.JobConnect.service.JobService;
import com.example.JobConnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/job-seeker")
public class JobSeekerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobApplicationService jobApplicationService;

    /**
     * Dashboard for Job Seekers
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User jobSeeker = userService.findByEmail(authentication.getName()).orElse(null);

        if (jobSeeker == null) {
            return "redirect:/login?error";
        }

        List<Job> jobs = jobService.getAllJobs();
        List<JobApplication> applications = jobApplicationService.getApplicationsByJobSeeker(jobSeeker);

        model.addAttribute("jobs", jobs);
        model.addAttribute("applications", applications);
        model.addAttribute("jobSeeker", jobSeeker);

        return "job-seeker/dashboard";
    }

    /**
     * Search Jobs
     */
    @GetMapping("/jobs/search")
    public String searchJobs(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             Model model) {
        List<Job> jobs;
        if ((keyword != null && !keyword.trim().isEmpty()) ||
            (location != null && !location.trim().isEmpty())) {
            jobs = jobService.searchJobs(keyword, location);
        } else {
            jobs = jobService.getAllJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);

        return "job-seeker/search-jobs";
    }

    /**
     * View Job Details
     */
    @GetMapping("/jobs/{id}")
    public String viewJobDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Job job = jobService.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/job-seeker/dashboard";
        }

        User jobSeeker = userService.findByEmail(authentication.getName()).orElse(null);
        boolean hasApplied = jobApplicationService
                .getApplicationsByJobSeeker(jobSeeker)
                .stream()
                .anyMatch(app -> app.getJob().getId().equals(id));

        model.addAttribute("job", job);
        model.addAttribute("hasApplied", hasApplied);

        return "job-seeker/job-details";
    }

    /**
     * Apply for a Job
     */
    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable Long id,
                              @RequestParam(value = "coverLetter", required = false) String coverLetter,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.findById(id).orElse(null);
            User jobSeeker = userService.findByEmail(authentication.getName()).orElse(null);

            if (job != null && jobSeeker != null) {
                // ✅ Prevent null coverLetter
                jobApplicationService.applyForJob(job, jobSeeker,
                        coverLetter != null ? coverLetter.trim() : "");

                redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid job or user.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/job-seeker/jobs/" + id;
    }

    /**
     * View My Applications
     */
    @GetMapping("/applications")
    public String viewMyApplications(Authentication authentication, Model model) {
        User jobSeeker = userService.findByEmail(authentication.getName()).orElse(null);

        if (jobSeeker == null) {
            return "redirect:/login?error";
        }

        List<JobApplication> applications = jobApplicationService.getApplicationsByJobSeeker(jobSeeker);
        model.addAttribute("applications", applications);

        return "job-seeker/my-applications";
    }
}
