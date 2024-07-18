package com.project.dto;

public class ProcessedLogDTO {
    private Long logId;
    private String produitLibelle;
    private String produitBarcode;
    private Long commandeId;
    private String description;
    private String status;
    private Integer quantity;
    private Long elementFactureId;

    // Constructors, getters, and setters

    public ProcessedLogDTO() {
    }

    public ProcessedLogDTO(String produitLibelle, String produitBarcode, Long commandeId, String description, String status, Integer quantity, Long elementFactureId) {
        this.produitLibelle = produitLibelle;
        this.produitBarcode = produitBarcode;
        this.commandeId = commandeId;
        this.description = description;
        this.status = status;
        this.quantity = quantity;
        this.elementFactureId = elementFactureId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getProduitLibelle() {
        return produitLibelle;
    }

    public void setProduitLibelle(String produitLibelle) {
        this.produitLibelle = produitLibelle;
    }

    public String getProduitBarcode() {
        return produitBarcode;
    }

    public void setProduitBarcode(String produitBarcode) {
        this.produitBarcode = produitBarcode;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getElementFactureId() {
        return elementFactureId;
    }

    public void setElementFactureId(Long elementFactureId) {
        this.elementFactureId = elementFactureId;
    }

    @Override
    public String toString() {
        return "ProcessedLogDTO{" +
                "produitLibelle='" + produitLibelle + '\'' +
                ", produitBarcode='" + produitBarcode + '\'' +
                ", commandeId=" + commandeId +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", quantity=" + quantity +
                ", elementFactureId=" + elementFactureId +
                '}';
    }
}

