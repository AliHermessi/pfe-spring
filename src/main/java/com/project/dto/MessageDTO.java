package com.project.dto;

public class MessageDTO {
    private Long senderId;
    private String content;

    public MessageDTO() {
    }

    // Getters and setters
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

