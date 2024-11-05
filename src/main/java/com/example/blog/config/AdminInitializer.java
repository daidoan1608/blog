package com.example.blog.config;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminAccount() {
        return args -> {
            if (userRepository.findByUsername("admin")!=null) {
                return;
            }
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setFullname("Admin");
            admin.setPhone("0123456789");
            admin.setAvatar("uploads/avatars/admin/admin.jpg");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.valueOf("ADMIN"));
            userRepository.save(admin);
            System.out.println("Tài khoản admin đã được tạo với username: admin và mật khẩu: admin123");
        };
    }
}
