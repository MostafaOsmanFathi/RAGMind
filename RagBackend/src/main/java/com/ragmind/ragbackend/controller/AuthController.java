package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.GenerateAccessTokenRequest;
import com.ragmind.ragbackend.dto.request.UserLoginRequest;
import com.ragmind.ragbackend.dto.request.UserSignupRequest;
import com.ragmind.ragbackend.dto.response.GenerateAccessTokenResponse;
import com.ragmind.ragbackend.dto.response.LoginResponse;
import com.ragmind.ragbackend.dto.response.SignupResponse;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.service.JwtService;
import com.ragmind.ragbackend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            LoginResponse loginResponse = userService.login(request);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));
        }
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userService.toResponse(user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            GenerateAccessTokenRequest generateAccessTokenRequest = new GenerateAccessTokenRequest();
            String email = jwtService.extractEmail(token);

            generateAccessTokenRequest.setRefreshToken(token);
            generateAccessTokenRequest.setEmail(email);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.generateAccessToken(generateAccessTokenRequest));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}

record ErrorResponse(String message) {
}