package com.project.models;
import java.time.LocalDateTime;
import java.util.Random;

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
    private Facture facture;

    private LocalDateTime dateCommande;
    private BigDecimal montantTotal;

    private String codeCommande;
    private String type_commande;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Fournisseur fournisseur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<ElementFacture> elementsFacture;


    public Commande() {

    }
    public String generateCodeCommande(Commande commande) {

        // Add type_commande

        Random random = new Random();

        int randomNumber = 1000 + random.nextInt(9000);

        return Integer.toString(randomNumber);
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


