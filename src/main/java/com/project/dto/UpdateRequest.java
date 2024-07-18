package com.project.dto;

public class UpdateRequest {
    private Long logId;
    private Long commandeId;
    private Long elementFactureId;
    private String description;
    private int quantity;
    private String status;

    public UpdateRequest() {
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public Long getElementFactureId() {
        return elementFactureId;
    }

    public void setElementFactureId(Long elementFactureId) {
        this.elementFactureId = elementFactureId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
// Getters and setters
}

