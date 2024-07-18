package com.project.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "description")
    private String description;

    @Column(name = "prix")
    private double prix;

    @Column(name = "cout")
    private Double cout;

    private int minStock;
    private int maxStock;

    private int unite;

    @Column(name = "tax")
    private int tax;

    @Column(name = "quantite")
    private int quantite;

    @Column(name = "date_arrivage")
    private String date_arrivage;

    @Column(name = "last_update")
    private String last_update;

    @Column(name = "status")
    private String status;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "brand")
    private String brand;

    private boolean isDisponible;

    @ManyToOne
    @JoinColumn(name = "categorie_id", referencedColumnName = "id")
    @JsonIgnore
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id", referencedColumnName = "id")
    @JsonIgnore
    private Fournisseur fournisseur;



    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ElementFacture> elementFactures;



    public static String generateBarcodeWithReturn(Produit produit) {
        StringBuilder barcodeBuilder = new StringBuilder();

        // Get the first letter from each word in libelle
        String[] words = produit.getLibelle().split("\\s+");
        for (String word : words) {
            if (!Character.isDigit(word.charAt(0))) { // Check if the first character is not a digit
                barcodeBuilder.append(word.charAt(0));
            } else {
                barcodeBuilder.append(word);
            }
        }
        Random random = new Random();
        int num1 = 100 + random.nextInt(900);
        barcodeBuilder.append("Produit"+num1);


        return barcodeBuilder.toString();
    }


    public Produit() {

    }

    public boolean isDisponible() {
        return isDisponible;
    }

    public void setDisponible(boolean disponible) {
        isDisponible = disponible;
    }

    public List<ElementFacture> getElementFactures() {
        return elementFactures;
    }

    public void setElementFactures(List<ElementFacture> elementFactures) {
        this.elementFactures = elementFactures;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public int getUnite() {
        return unite;
    }

    public void setUnite(int unite) {
        this.unite = unite;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Double getCout() {
        return cout;
    }

    public void setCout(Double cout) {
        this.cout = cout;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getDate_arrivage() {
        return date_arrivage;
    }

    public void setDate_arrivage(String date_arrivage) {
        this.date_arrivage = date_arrivage;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }




}