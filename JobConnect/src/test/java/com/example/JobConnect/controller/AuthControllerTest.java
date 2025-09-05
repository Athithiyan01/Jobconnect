package com.example.JobConnect.controller;

import com.example.JobConnect.dto.UserRegistrationDto;
import com.example.JobConnect.entity.Role;
import com.example.JobConnect.entity.User;
import com.example.JobConnect.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser"); // required in service
        dto.setFullName("Test User");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setRole(Role.JOB_SEEKER);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.JOB_SEEKER);

        // ✅ fix: use registerUser
        Mockito.when(userService.registerUser(any(UserRegistrationDto.class)))
               .thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
