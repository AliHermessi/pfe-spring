package com.project.dto;

public class AuthRequest {

    private String cin;
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String cin, String password) {
        this.cin = cin;
        this.password = password;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
