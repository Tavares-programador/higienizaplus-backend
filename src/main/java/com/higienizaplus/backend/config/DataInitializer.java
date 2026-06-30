package com.higienizaplus.backend.config;

import com.higienizaplus.backend.model.Admin;
import com.higienizaplus.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-username}")
    private String defaultUsername;

    @Value("${app.admin.default-password}")
    private String defaultPassword;

    @Override
    public void run(String... args) {
        if (!adminRepository.existsByUsername(defaultUsername)) {
            Admin admin = Admin.builder()
                    .username(defaultUsername)
                    .passwordHash(passwordEncoder.encode(defaultPassword))
                    .build();
            adminRepository.save(admin);
            log.info("Admin padrão criado: username='{}'. TROQUE A SENHA PADRÃO EM PRODUÇÃO.", defaultUsername);
        }
    }
}
