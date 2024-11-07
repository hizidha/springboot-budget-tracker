package com.devland.assignment.finalproject.authentication.model.dto;

import lombok.Getter;

@Getter
public class JwtResponseDTO {
    private final String type = "Bearer";
    private final String token;
    private final UserLoginResponseDTO user;

    public JwtResponseDTO(String jwtToken, UserLoginResponseDTO user) {
        this.token = jwtToken;
        this.user = user;
    }
}