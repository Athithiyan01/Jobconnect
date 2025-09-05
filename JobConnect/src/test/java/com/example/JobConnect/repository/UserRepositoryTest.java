package com.example.JobConnect.repository;

import com.example.JobConnect.entity.Role;
import com.example.JobConnect.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        // ✅ Create user with all required fields
        User user = new User();
        user.setUsername("repo_user");       // Required field
        user.setFullName("Repo Test");
        user.setEmail("repo@example.com");
        user.setPassword("pass123");
        user.setRole(Role.JOB_SEEKER);       // ✅ Role enum, not string

        // Save
        userRepository.save(user);

        // Retrieve
        Optional<User> found = userRepository.findByEmail("repo@example.com");

        // Verify
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Repo Test");
        assertThat(found.get().getUsername()).isEqualTo("repo_user");  // extra check
        assertThat(found.get().getRole()).isEqualTo(Role.JOB_SEEKER);  // ✅ check role too
    }
}
