package com.propsightai.Config;

import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Role.AuthProvider;
import com.propsightai.Role.Role;
import com.propsightai.Role.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setName("PropSight Admin");
            admin.setEmail("admin2@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Sets password as admin123
            admin.setPhone("+923326687056");
            admin.setUserType(Role.ADMIN);
            admin.setAuthProvider(AuthProvider.LOCAL);
            admin.setActive(true);
            admin.setEmailVerified(true);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setCreatedAt(LocalDate.now());
            admin.setTotalListings(0);

            userRepository.save(admin);
            System.out.println("✨ System administrator successfully seeded: admin@gmail.com");
        }
    }
}