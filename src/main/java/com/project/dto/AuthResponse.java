package com.project.dto;

import java.util.List;

public class AuthResponse {

    private List<String> roles;

    public AuthResponse(List<String> roles) {
        this.roles = roles;
    }

    // getters and setters
}
