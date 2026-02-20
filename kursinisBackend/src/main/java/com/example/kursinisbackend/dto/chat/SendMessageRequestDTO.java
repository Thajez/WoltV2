package com.example.kursinisbackend.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequestDTO {
    @NotBlank private String text;
    @NotNull private Integer senderId;
}
