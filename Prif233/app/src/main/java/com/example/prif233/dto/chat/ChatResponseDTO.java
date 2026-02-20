package com.example.prif233.dto.chat;

import java.util.List;

public class ChatResponseDTO {
    private int id;
    private String name;
    private int orderId;
    private String orderStatus;
    private String dateCreated;
    private List<MessageResponseDTO> messages;

    public ChatResponseDTO() {}

    public ChatResponseDTO(int id, String name, int orderId, String orderStatus, String dateCreated, List<MessageResponseDTO> messages) {
        this.id = id;
        this.name = name;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.dateCreated = dateCreated;
        this.messages = messages;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public List<MessageResponseDTO> getMessages() { return messages; }
    public void setMessages(List<MessageResponseDTO> messages) { this.messages = messages; }
}
