package com.example.kursinisbackend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordUpdateRequestDTO {
    @NotBlank private String oldPassword;
    @NotBlank private String newPassword1;
    @NotBlank private String newPassword2;
}
