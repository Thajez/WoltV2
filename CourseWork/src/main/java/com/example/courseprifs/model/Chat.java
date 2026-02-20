package com.example.courseprifs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private LocalDate dateCreated;

    @OneToOne(optional = false)
    private FoodOrder foodOrder;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    public Chat(String name, FoodOrder foodOrder) {
        this.name = name;
        this.foodOrder = foodOrder;
        this.dateCreated = LocalDate.now();
        this.messages = new ArrayList<>();
    }
}
