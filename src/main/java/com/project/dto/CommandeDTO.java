package com.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CommandeDTO {

    private Long id;
    private String codeCommande;
    private String codeFacture;
    private LocalDateTime dateCommande;
    private BigDecimal montantTotal;
    private String adresse;
    private String fournisseurName;
    private String numero;
    private String clientName;
    private Long fournisseur_id;
    private Long client_id;
    private double montantTotalht;
    private double montantTotalttc;

    private double totalTax;
    private double totalRemise;
    private boolean isFactureGenerated;
    private String type_commande;
    private List<ElementFactureDTO> elementsFacture;
    public CommandeDTO() {
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public boolean isFactureGenerated() {
        return isFactureGenerated;
    }

    public void setFactureGenerated(boolean factureGenerated) {
        isFactureGenerated = factureGenerated;
    }

    public Long getFournisseur_id() {
        return fournisseur_id;
    }

    public void setFournisseur_id(Long fournisseur_id) {
        this.fournisseur_id = fournisseur_id;
    }

    public Long getClient_id() {
        return client_id;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }

    public List<ElementFactureDTO> getElementsFacture() {
    return elementsFacture;
}

public void setElementsFacture(List<ElementFactureDTO> elementFactures) {
    this.elementsFacture = elementFactures;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeFacture() {
        return codeFacture;
    }

    public void setCodeFacture(String codeFacture) {
        this.codeFacture = codeFacture;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getFournisseurName() {
        return fournisseurName;
    }

    public void setFournisseurName(String fournisseurName) {
        this.fournisseurName = fournisseurName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


}
