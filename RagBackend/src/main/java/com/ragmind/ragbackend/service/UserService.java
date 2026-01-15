package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.request.UserLoginRequest;
import com.ragmind.ragbackend.dto.request.UserSignupRequest;
import com.ragmind.ragbackend.dto.response.UserResponse;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserSignupRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        //TODO Hash Password
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());

        return userRepository.save(user);
    }

    public UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
    }


    public User login(UserLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        //TODO Compare Hashed Password

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }


}
