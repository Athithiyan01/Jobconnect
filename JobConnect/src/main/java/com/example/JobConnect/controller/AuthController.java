package com.example.JobConnect.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.JobConnect.dto.UserRegistrationDto;
import com.example.JobConnect.entity.Role;
import com.example.JobConnect.service.UserService;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("roles", Role.values());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid UserRegistrationDto userDto, 
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "register";
        }
        
        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYER"))) {
            return "redirect:/employer/dashboard";
        } else {
            return "redirect:/job-seeker/dashboard";
        }
    }
}