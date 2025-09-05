package com.example.JobConnect.controller;




import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.JobConnect.dto.UserRegistrationDto;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto dto) {
        User savedUser = userService.registerUser(dto);
        return ResponseEntity.ok(savedUser);
    }
}
