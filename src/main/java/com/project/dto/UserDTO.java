package com.project.dto;


import java.util.List;

public class UserDTO {







        private Long id;


        private String username;


        private String cin;


        private String password;


    private List<String> role;




        public UserDTO() {
        }

    public UserDTO(Long id, String username, String cin, String password, List<String> role) {
        this.id = id;
        this.username = username;
        this.cin = cin;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
}
