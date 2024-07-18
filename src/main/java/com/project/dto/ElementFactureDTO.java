package com.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElementFactureDTO {
private long elementFactureId;
    @JsonProperty("ProduitId")
    private long produitId;
private String refProduit;
    private String libelle;
    private int quantity;
    private double prix;
    private double tax;
    private double remise;
    private double netHT;
    private double netTTC;

    private int minStock;

    public ElementFactureDTO() {
    }

    public long getElementFactureId() {
        return elementFactureId;
    }

    public void setElementFactureId(long elementFactureId) {
        this.elementFactureId = elementFactureId;
    }

    public long getProduitId() {
        return produitId;
    }

    public void setProduitId(long produitId) {
        this.produitId = produitId;
    }

    public String getRefProduit() {
        return refProduit;
    }

    public void setRefProduit(String refProduit) {
        this.refProduit = refProduit;
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

    // Getters and Setters
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    // toString method
    @Override
    public String toString() {
        return "ElementFactureDTO{" +
                "libelle='" + libelle + '\'' +
                ", quantity=" + quantity +
                ", prix=" + prix +
                ", tax=" + tax +
                ", remise=" + remise +
                '}';
    }
}

