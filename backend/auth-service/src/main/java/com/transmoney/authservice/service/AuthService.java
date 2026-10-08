package com.transmoney.authservice.service;

import com.transmoney.authservice.dto.LoginRequest;
import com.transmoney.authservice.dto.LoginResponse;
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
    private final JwtService jwtService;

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


    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse( token);
    }
}