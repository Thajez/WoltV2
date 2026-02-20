package com.example.kursinisbackend.dto.chat;

import lombok.Data;

@Data
public class MessageResponseDTO {
    private int id;
    private String text;
    private int senderId;
    private String senderName;
    private String sentAt;
}
