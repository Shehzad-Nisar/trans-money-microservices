package com.transmoney.authservice.service;

import com.transmoney.authservice.dto.RegisterRequest;
import com.transmoney.authservice.entity.Role;
import com.transmoney.authservice.entity.User;
import com.transmoney.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setEmail(request.getEmail());

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        user.setPasswordHash(passwordHash);

        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
    }
}