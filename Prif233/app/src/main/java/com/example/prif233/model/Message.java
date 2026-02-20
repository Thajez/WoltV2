package com.example.prif233.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private int id;
    private String text;
    private LocalDateTime sentAt;
    private BasicUser sender;
    private Chat chat;

    public Message() {}

    public Message(String text, BasicUser sender, Chat chat) {
        this.text = text;
        this.sender = sender;
        this.chat = chat;
        this.sentAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public BasicUser getSender() { return sender; }
    public void setSender(BasicUser sender) { this.sender = sender; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }
}