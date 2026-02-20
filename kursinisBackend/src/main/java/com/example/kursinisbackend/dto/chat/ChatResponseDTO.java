package com.example.kursinisbackend.dto.chat;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseDTO {
    private int id;
    private String name;
    private int orderId;
    private String orderStatus;
    private String dateCreated;
    private List<MessageResponseDTO> messages;
}
