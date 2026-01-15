package com.ragmind.ragbackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
class AuthController {

    @PostMapping("/login")
    void login() {

    }

    @PostMapping("/signup")
    void signup() {

    }

    @PostMapping("/refreshtoken")
    void refreshToken(){

    }

}
