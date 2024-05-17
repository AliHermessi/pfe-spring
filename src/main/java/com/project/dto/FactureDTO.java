package com.project.dto;

import com.project.models.Commande;
import com.project.models.ElementFacture;
import com.project.models.Produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FactureDTO {
    private Long id;
    private String code;
    private BigDecimal montantTotal;
    private LocalDateTime dateFacture;
    private String address;
    private String nomClient;
    private String numero;

    private String nomFournisseur;
    private String type_facture;
    private List<ElementFactureDTO> elementFacturesDTO;
    private Commande commande;
    private boolean generated;

    private double montantTotalht;
    private double montantTotalttc;

    private double totalTax;
    private double totalRemise;
    public FactureDTO() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<ElementFactureDTO> getElementFacturesDTO() {
        return elementFacturesDTO;
    }

    public void setElementFacturesDTO(List<ElementFactureDTO> elementFacturesDTO) {
        this.elementFacturesDTO = elementFacturesDTO;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public String getType_facture() {
        return type_facture;
    }

    public void setType_facture(String type_facture) {
        this.type_facture = type_facture;
    }

    public List<ElementFactureDTO> getElementFactures() {
        return elementFacturesDTO;
    }

    public void setElementFactures(List<ElementFactureDTO> elementFactures) {
        this.elementFacturesDTO = elementFactures;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
