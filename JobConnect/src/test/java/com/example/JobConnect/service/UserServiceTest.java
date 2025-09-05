package com.example.JobConnect.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.JobConnect.dto.UserRegistrationDto;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;  // mock it

    @InjectMocks
    private UserService userService;  // inject mocks here

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password");

        // mock password encoding
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // mock repo save
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User user = userService.registerUser(dto);

        assertNotNull(user);
        assertEquals("encodedPassword", user.getPassword());
    }
}
