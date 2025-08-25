package com.example.JobConnect.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.JobConnect.dto.JobDto;
import com.example.JobConnect.entity.ApplicationStatus;
import com.example.JobConnect.entity.Job;
import com.example.JobConnect.entity.JobApplication;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.service.JobApplicationService;
import com.example.JobConnect.service.JobService;
import com.example.JobConnect.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User employer = userService.findByEmail(authentication.getName()).orElse(null);
        List<Job> jobs = jobService.getJobsByEmployer(employer);
        model.addAttribute("jobs", jobs);
        model.addAttribute("employer", employer);
        return "employer/dashboard";
    }
    
    @GetMapping("/jobs/create")
    public String showCreateJobForm(Model model) {
        model.addAttribute("job", new JobDto());
        return "employer/create-job";
    }
    
    @PostMapping("/jobs/create")
    public String createJob(@Valid JobDto jobDto,
                           BindingResult bindingResult,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "employer/create-job";
        }
        
        User employer = userService.findByEmail(authentication.getName()).orElse(null);
        jobService.createJob(jobDto, employer);
        redirectAttributes.addFlashAttribute("successMessage", "Job posted successfully!");
        return "redirect:/employer/dashboard";
    }
    
    @GetMapping("/jobs/{id}/edit")
    public String showEditJobForm(@PathVariable Long id, Model model, Authentication authentication) {
        Job job = jobService.findById(id).orElse(null);
        User employer = userService.findByEmail(authentication.getName()).orElse(null);
        
        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/dashboard";
        }
        
        JobDto jobDto = new JobDto();
        jobDto.setTitle(job.getTitle());
        jobDto.setDescription(job.getDescription());
        jobDto.setLocation(job.getLocation());
        jobDto.setSalary(job.getSalary());
        jobDto.setDeadline(job.getDeadline());
        
        model.addAttribute("job", jobDto);
        model.addAttribute("jobId", id);
        return "employer/edit-job";
    }
    
    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                           @Valid JobDto jobDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/edit-job";
        }
        
        jobService.updateJob(id, jobDto);
        redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
        return "redirect:/employer/dashboard";
    }
    
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jobService.deleteJob(id);
        redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
        return "redirect:/employer/dashboard";
    }
    
    @GetMapping("/jobs/{id}/applications")
    public String viewJobApplications(@PathVariable Long id, Model model, Authentication authentication) {
        Job job = jobService.findById(id).orElse(null);
        User employer = userService.findByEmail(authentication.getName()).orElse(null);
        
        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/dashboard";
        }
        
        List<JobApplication> applications = jobApplicationService.getApplicationsByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "employer/job-applications";
    }
    
    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                        @RequestParam ApplicationStatus status,
                                        RedirectAttributes redirectAttributes) {
        JobApplication application = jobApplicationService.findById(id).orElse(null);
        if (application != null) {
            jobApplicationService.updateApplicationStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Application status updated!");
            return "redirect:/employer/jobs/" + application.getJob().getId() + "/applications";
        }
        return "redirect:/employer/dashboard";
    }
}
