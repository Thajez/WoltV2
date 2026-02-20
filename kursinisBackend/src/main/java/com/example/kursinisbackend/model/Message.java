package com.example.kursinisbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;
    private LocalDateTime sentAt;

    @ManyToOne(optional = false)
    private BasicUser sender;

    @ManyToOne(optional = false)
    private Chat chat;

    public Message(String text, BasicUser sender, Chat chat) {
        this.text = text;
        this.sender = sender;
        this.chat = chat;
        this.sentAt = LocalDateTime.now();
    }
}