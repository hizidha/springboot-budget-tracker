package com.devland.assignment.finalproject.authentication.model.dto;

import lombok.*;

@Getter
public class RegisterResponseDTO {
    private final String message = "User registered successfully";
    private final UserResponseDTO user;

    public RegisterResponseDTO(UserResponseDTO user) {
        this.user = user;
    }
}