package com.project.dto;

import java.util.List;

public class FournisseurDTO {
    private Long id;
    private String nom;
    private String address;
    private String numero;

    private List<Long> ListIdCommande;
    private List<Long> ListIdProduit;

    public FournisseurDTO() {
    }

    public FournisseurDTO(Long id, String nom, String address, String numero) {
        this.id = id;
        this.nom = nom;
        this.address = address;
        this.numero = numero;
    }

    public List<Long> getListIdCommande() {
        return ListIdCommande;
    }

    public void setListIdCommande(List<Long> listIdCommande) {
        ListIdCommande = listIdCommande;
    }

    public List<Long> getListIdProduit() {
        return ListIdProduit;
    }

    public void setListIdProduit(List<Long> listIdProduit) {
        ListIdProduit = listIdProduit;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}

