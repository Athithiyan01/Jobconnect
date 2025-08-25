package com.example.JobConnect.service;

import com.example.JobConnect.entity.User;
import com.example.JobConnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔹 fetch User entity by email instead of username
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 🔹 convert to Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),                         // use email for login
                user.getPassword(),                      // password (BCrypt hashed)
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())) // authorities
        );
    }
}
