package com.example.prif233.model;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {

    private int id;
    private String name;
    private LocalDate dateCreated;
    private FoodOrder foodOrder;
    private List<Message> messages = new ArrayList<>();

    public Chat() {}

    public Chat(String name, FoodOrder foodOrder) {
        this.name = name;
        this.foodOrder = foodOrder;
        this.dateCreated = LocalDate.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }

    public FoodOrder getFoodOrder() { return foodOrder; }
    public void setFoodOrder(FoodOrder foodOrder) { this.foodOrder = foodOrder; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
