package com.example.kursinisbackend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientCreateRequestDTO {
    @NotBlank private String login;
    @NotBlank private String password;
    @NotBlank private String name;
    @NotBlank private String surname;
    @NotBlank private String phoneNumber;
    @NotBlank private String address;
}
