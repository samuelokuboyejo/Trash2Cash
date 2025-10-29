package com.trash2cash.bootstrap;

import com.trash2cash.auth.service.JwtService;
import com.trash2cash.auth.service.RefreshTokenService;
import com.trash2cash.users.enums.Status;
import com.trash2cash.users.enums.UserRole;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findByRole(UserRole.ADMIN).isEmpty()) {
                User admin = User.builder()
                        .email("admin")
                        .firstName("Admin")
                        .password(encoder.encode("SuperSecurePassword123"))
                        .role(UserRole.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .status(Status.ACTIVE)
                        .build();
                userRepository.save(admin);
                var accessToken = jwtService.generateToken(admin);
                var refreshToken = refreshTokenService.createRefreshToken(admin.getEmail());
                log.debug(" Default admin account created! " + "/n accessToken: " + accessToken + "/n refreshToken: " + refreshToken);
            }
        };
    }


}
