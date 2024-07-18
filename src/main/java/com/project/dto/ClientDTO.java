package com.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;

import java.util.List;

public class ClientDTO {
    private Long id;

    private String nom;

    private String email;

    private String numero_telephone;

    private String address;

    @JsonIgnore
    private List<Long> ListIdCommande;
    public ClientDTO() {

    }

    public List<Long> getListIdCommande() {
        return ListIdCommande;
    }

    public void setListIdCommande(List<Long> listIdCommande) {
        ListIdCommande = listIdCommande;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumero_telephone() {
        return numero_telephone;
    }

    public void setNumero_telephone(String numero_telephone) {
        this.numero_telephone = numero_telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
