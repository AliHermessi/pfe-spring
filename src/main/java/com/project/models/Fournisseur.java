package com.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "fournisseur")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "address")
    private String address;

    @Column(name="numero")
    private String numero;

    @OneToMany
    @JsonIgnore
    private List<Produit> produits;

    @OneToMany (mappedBy = "fournisseur")
    @JsonIgnore
    private List<Commande> commandes;

    public Fournisseur() {

    }

    public Fournisseur(Long id, String nom, String address,String numero, List<Commande> commandes,List<Produit> produits) {
        this.id = id;
        this.nom = nom;
        this.address = address;
        this.produits = produits;
        this.numero=numero;
        this.commandes=commandes;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
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

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }
}
