package com.ragmind.ragbackend.dto;

import java.util.List;

public class UserDTO {

    private Long id;
    private String name;
    private boolean userEnabled;
    private String email;
    private String password;
    private String refreshToken;
    private String phoneNumber;


    public UserDTO() {
    }

    public UserDTO(Long id, String name, boolean userEnabled, String email,
                   String password, String refreshToken, String phoneNumber,
                   List<Long> userCollectionIds, List<Long> notificationIds) {
        this.id = id;
        this.name = name;
        this.userEnabled = userEnabled;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.phoneNumber = phoneNumber;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUserEnabled() {
        return userEnabled;
    }

    public void setUserEnabled(boolean userEnabled) {
        this.userEnabled = userEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
