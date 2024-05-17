package com.project.models;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "elementfacture")
public class ElementFacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refcode;
    private String libelle;
    private int quantity;
    private double tax;
    private double prix;
    private double remise;

    private double netHT;
    private double netTTC;

    @ManyToOne
    @JoinColumn(name = "commande_id", referencedColumnName = "id")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "facture_id", referencedColumnName = "id")
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", referencedColumnName = "id")
    private Produit produit;

    public ElementFacture() {
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public double getNetHT() {
        return netHT;
    }

    public void setNetHT(double netHT) {
        this.netHT = netHT;
    }

    public double getNetTTC() {
        return netTTC;
    }

    public void setNetTTC(double netTTC) {
        this.netTTC = netTTC;
    }

    public String getRefcode() {
        return refcode;
    }

    public void setRefcode(String refcode) {
        this.refcode = refcode;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }



    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }


}
