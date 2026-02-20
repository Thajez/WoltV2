package com.example.kursinisbackend.dto.user;

import com.example.kursinisbackend.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class DriverUpdateRequestDTO extends ClientUpdateRequestDTO {
    @NotBlank private String licence;
    @NotNull private LocalDate bDate;
    @NotNull private VehicleType vehicleType;
}
