package com.example.kursinisbackend.dto.user;

import com.example.kursinisbackend.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverCreateRequestDTO {
    @NotBlank private String login;
    @NotBlank private String password;
    @NotBlank private String name;
    @NotBlank private String surname;
    @NotBlank private String phoneNumber;
    @NotBlank private String address;

    @NotBlank private String licence;
    @NotNull private LocalDate bDate;
    @NotNull private VehicleType vehicleType;
}
