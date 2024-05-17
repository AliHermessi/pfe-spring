package com.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "facture")

public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Commande commande;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ElementFacture> elementsFacture;


@Column(name = "generate")
private Boolean generated;
    private String code;
    private BigDecimal montantTotal;
    private double montantTotalht;
    private double montantTotalttc;

    private double totalTax;
    private double totalRemise;
    private LocalDateTime dateFacture;


    public Facture() {
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
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

    public double getTotalRemise() {
        return totalRemise;
    }

    public void setTotalRemise(double totalRemise) {
        this.totalRemise = totalRemise;
    }

    public static String generateFactureCode() {
        return "FACT-" + UUID.randomUUID().toString();
    }
    public Boolean getGenerated() {
        return generated;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    public List<ElementFacture> getElementsFacture() {
        return elementsFacture;
    }

    public void setElementsFacture(List<ElementFacture> elementsFacture) {
        this.elementsFacture = elementsFacture;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateCommande) {
        this.dateFacture = dateCommande;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }



    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }




}
