package com.project.models;

import jakarta.persistence.*;

@Entity
@Table(name = "entreprise")
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomEntreprise;
    private String adresse;
    private String codePostal;
    private String telephone;
    private String mobile;
    private String email;
    private String codeTVA;
    private String identifiantUnique;
    private String compteCourant;


    public Entreprise() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodeTVA() {
        return codeTVA;
    }

    public void setCodeTVA(String codeTVA) {
        this.codeTVA = codeTVA;
    }

    public String getIdentifiantUnique() {
        return identifiantUnique;
    }

    public void setIdentifiantUnique(String identifiantUnique) {
        this.identifiantUnique = identifiantUnique;
    }

    public String getCompteCourant() {
        return compteCourant;
    }

    public void setCompteCourant(String compteCourant) {
        this.compteCourant = compteCourant;
    }
}
