package com.project.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;
public class PVE {

        private Long produitId;
        private String barcode;
        private String libelle;
        private String type_commande;
        private int quantity;
        private double prix;
        private double cout;
        private double netHT;
        private double totalCout;
        private double total;
        private LocalDateTime date;
        private String fournisseurName;

        private String categorieName;
        private String clientName;

    public PVE() {
    }

    public PVE(Long produitId, String barcode, String libelle, String type_commande, int quantity,
               double prix, double cout, double netHT, double totalCout,
               double total, LocalDateTime date, String fournisseurName,
               String categorieName, String clientName) {
        this.produitId = produitId;
        this.barcode = barcode;
        this.libelle = libelle;
        this.type_commande = type_commande;
        this.quantity = quantity;
        this.prix = prix;
        this.cout = cout;
        this.netHT = netHT;
        this.totalCout = totalCout;
        this.total = total;
        this.date = date;
        this.fournisseurName = fournisseurName;
        this.categorieName = categorieName;
        this.clientName = clientName;
    }

    public double getTotalCout() {
        return totalCout;
    }

    public void setTotalCout(double totalCout) {
        this.totalCout = totalCout;
    }


    public String getType_commande() {
        return type_commande;
    }

    public void setType_commande(String type_commande) {
        this.type_commande = type_commande;
    }

    public double getNetHT() {
        return netHT;
    }

    public void setNetHT(double netHT) {
        this.netHT = netHT;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public double getNetht() {
        return netHT;
    }

    public void setNetht(double netht) {
        this.netHT = netht;
    }

    public long getProduitId() {
        return produitId;
    }

    public void setProduitId(long produitId) {
        this.produitId = produitId;
    }

    public String getCategorieName() {
        return categorieName;
    }

    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        clientName = clientName;
    }

    public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
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

        public double getPrix() {
            return prix;
        }

        public void setPrix(double prix) {
            this.prix = prix;
        }

        public double getCout() {
            return cout;
        }

        public void setCout(double cout) {
            this.cout = cout;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public String getFournisseurName() {
            return fournisseurName;
        }

        public void setFournisseurName(String fournisseurName) {
            this.fournisseurName = fournisseurName;
        }





}
