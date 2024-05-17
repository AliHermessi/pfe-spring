package com.project.dto;

import java.time.LocalDateTime;

public class userActivityDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userRole;

    private String libelle;

    private double prix;

    private double cout;

    private String action;

    private String quantite;

    private LocalDateTime timestamp;

    private String description;

    public userActivityDTO() {
    }

    public userActivityDTO(Long id, String libelle, Long userId, String userName, String userRole, double prix, double cout, String action,
                           String quantite, LocalDateTime timestamp, String description) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.prix = prix;
        this.cout = cout;
        this.action = action;
        this.quantite = quantite;
        this.timestamp = timestamp;
        this.description = description;
        this.libelle=libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

