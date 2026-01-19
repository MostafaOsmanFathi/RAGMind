package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.GenerateAccessTokenRequest;
import com.ragmind.ragbackend.dto.request.UserLoginRequest;
import com.ragmind.ragbackend.dto.request.UserSignupRequest;
import com.ragmind.ragbackend.dto.response.GenerateAccessTokenResponse;
import com.ragmind.ragbackend.dto.response.LoginResponse;
import com.ragmind.ragbackend.dto.response.SignupResponse;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ExceptionHandler(Exception.class)
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
    @ExceptionHandler(Exception.class)
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
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> refreshToken(@RequestBody GenerateAccessTokenRequest generateAccessTokenRequest) {
        try {
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