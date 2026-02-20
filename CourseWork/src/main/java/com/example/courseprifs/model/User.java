package com.example.courseprifs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(unique = true)
    protected String login;
    protected String password;
    protected String name;
    protected String surname;
    protected String phoneNumber;
    protected LocalDateTime dateCreated;
    protected LocalDateTime dateUpdated;
    @Enumerated(EnumType.STRING)
    protected UserType userType;

    @Column(nullable = false)
    private boolean active = true;

    public User(String login, String password, String name, String surname, String phoneNumber, UserType userType) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }
    @Override
    public String toString() {
        return  name + " " + surname;
    }
}
