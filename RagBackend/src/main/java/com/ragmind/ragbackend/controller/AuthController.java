package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.UserLoginRequest;
import com.ragmind.ragbackend.dto.request.UserSignupRequest;
import com.ragmind.ragbackend.dto.response.LoginResponse;
import com.ragmind.ragbackend.dto.response.SignupResponse;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @RequestBody UserLoginRequest request) {

        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    ResponseEntity<SignupResponse> signup(@RequestBody UserSignupRequest request) {

        User user = userService.createUser(request);

        return ResponseEntity
                .status(201)
                .body(userService.toResponse(user));
    }

    @PostMapping("/refreshtoken")
    void refreshToken(){

    }

}
