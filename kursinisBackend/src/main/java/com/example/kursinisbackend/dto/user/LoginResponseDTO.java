package com.example.kursinisbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private int id;
    private String login;
    private String name;
    private String surname;
    private String userType;
}
