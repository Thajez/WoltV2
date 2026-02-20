package com.example.prif233.dto.chat;

public class MessageResponseDTO {
    private int id;
    private String text;
    private int senderId;
    private String senderName;
    private String sentAt;

    public MessageResponseDTO() {}

    public MessageResponseDTO(int id, String text, int senderId, String senderName, String sentAt) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.senderName = senderName;
        this.sentAt = sentAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return senderName + ": " + text;
    }
}