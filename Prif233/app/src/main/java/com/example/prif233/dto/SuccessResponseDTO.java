package com.example.prif233.dto;

public class SuccessResponseDTO {
    private String message;

    public SuccessResponseDTO(String message) {
        this.message = message;
    }

    public SuccessResponseDTO () {}

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
