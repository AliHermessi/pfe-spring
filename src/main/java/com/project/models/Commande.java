package com.project.models;
import java.time.LocalDateTime;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "commande")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "facture_id", referencedColumnName = "id")
    @JsonManagedReference
    private Facture facture;

    @JsonIgnore
    private LocalDateTime dateCommande;
    private BigDecimal montantTotal;
    private double montantTotalht;
    private double montantTotalttc;

    private String addressLivraison;
    private double totalTax;
    private double totalRemise;

    private String codeCommande;
    private String type_commande;

    private boolean BDLisPrinted;
    @ManyToOne
    private Client client;

    @ManyToOne
    private Fournisseur fournisseur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ElementFacture> elementsFacture;


    public Commande() {

    }
    public String generateCodeCommande(Commande commande) {

        // Add type_commande

        Random random = new Random();

        int randomNumber = 1000 + random.nextInt(9000);

        return Integer.toString(randomNumber);
    }

    public boolean isBDLisPrinted() {
        return BDLisPrinted;
    }

    public void setBDLisPrinted(boolean BDLisPrinted) {
        this.BDLisPrinted = BDLisPrinted;
    }

    public String getAddressLivraison() {
        return addressLivraison;
    }

    public void setAddressLivraison(String addressLivraison) {
        this.addressLivraison = addressLivraison;
    }

    public double getMontantTotalht() {
        return montantTotalht;
    }

    public void setMontantTotalht(double montantTotalht) {
        this.montantTotalht = montantTotalht;
    }

    public double getMontantTotalttc() {
        return montantTotalttc;
    }

    public void setMontantTotalttc(double montantTotalttc) {
        this.montantTotalttc = montantTotalttc;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public double getTotalRemise() {
        return totalRemise;
    }

    public void setTotalRemise(double totalRemise) {
        this.totalRemise = totalRemise;
    }

    public List<ElementFacture> getElementsFacture() {
        return elementsFacture;
    }

    public void setElementsFacture(List<ElementFacture> elementsFacture) {
        this.elementsFacture = elementsFacture;
    }

    public String getCodeCommande() {
        return codeCommande;
    }

    public void setCodeCommande(String codeCommande) {
        this.codeCommande = codeCommande;
    }

    public String getType_commande() {
        return type_commande;
    }

    public void setType_commande(String type_commande) {
        this.type_commande = type_commande;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


}


