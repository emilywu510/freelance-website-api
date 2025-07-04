package com.emily.freelance.service;


import com.emily.freelance.Application;
import com.emily.freelance.entity.User;
import com.emily.freelance.repository.UserRepository;
import com.emily.freelance.request.LoginRequest;
import com.emily.freelance.response.LoginResponse;
import com.emily.freelance.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists.");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return "User registered.";
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isEmpty()) {
            throw new RuntimeException("Invalid credentials.");
        }

        User user = existing.get();

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }

        String token = jwtUtil.generateToken(user.getId());
        logger.info("userId :" + user.getId().toString());
        logger.info("token: " + token);
        return new LoginResponse(token, user.getId());

    }
}