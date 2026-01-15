package com.ragmind.ragbackend.dto.response;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String longLivedToken;
    private String refreshToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLongLivedToken() {
        return longLivedToken;
    }

    public void setLongLivedToken(String longLivedToken) {
        this.longLivedToken = longLivedToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
