package com.project.models;
import jakarta.persistence.*;


import java.util.List;
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "cin", unique = true, nullable = false)
    private String cin;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserActivity> activities;

    public User() {
    }

    public User(Long id, String username, String cin, String password, List<Role> role, List<UserActivity> activities) {
        this.id = id;
        this.username = username;
        this.cin = cin;
        this.password = password;
        this.role = role;
        this.activities = activities;
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

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public List<UserActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<UserActivity> activities) {
        this.activities = activities;
    }
}
