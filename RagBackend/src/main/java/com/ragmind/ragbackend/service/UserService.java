package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.request.GenerateAccessTokenRequest;
import com.ragmind.ragbackend.dto.request.UserLoginRequest;
import com.ragmind.ragbackend.dto.request.UserSignupRequest;
import com.ragmind.ragbackend.dto.response.GenerateAccessTokenResponse;
import com.ragmind.ragbackend.dto.response.LoginResponse;
import com.ragmind.ragbackend.dto.response.SignupResponse;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User createUser(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This Email Already Exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());


        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUserEnabled(true);

        return userRepository.save(user);
    }

    public SignupResponse toResponse(User user) {

        SignupResponse response = new SignupResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
    }


    public LoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ValidationException("Email doesn't exists"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        String accessToken = jwtService.generateAccessToken(new HashMap<>(), user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setEmail(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return loginResponse;
    }

    public GenerateAccessTokenResponse generateAccessToken(GenerateAccessTokenRequest request) throws Exception {
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new Exception("Refresh Token is not valid or expired");
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ValidationException("Token Email doesn't exists"));
        if (!user.isUserEnabled()) {
            throw new Exception("user not enabled");
        }
        if (!user.getRefreshToken().equals(request.getRefreshToken())) {
            throw new Exception("Refresh Token Expired login again");
        }
        
        String accessToken = jwtService.generateAccessToken(new HashMap<>(), user);
        GenerateAccessTokenResponse response = new GenerateAccessTokenResponse();
        response.setAccessToken(accessToken);
        return response;
    }
}
