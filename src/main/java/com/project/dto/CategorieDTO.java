package com.project.dto;

import java.util.List;

public class CategorieDTO {

    private Long id;
    private String nom;
    private String description;

private List<Long> ListIdProduit;
    public CategorieDTO() {
    }

    // Parameterized constructor
    public CategorieDTO(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public List<Long> getListIdProduit() {
        return ListIdProduit;
    }

    public void setListIdProduit(List<Long> listIdProduit) {
        ListIdProduit = listIdProduit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString method
    @Override
    public String toString() {
        return "CategorieDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

