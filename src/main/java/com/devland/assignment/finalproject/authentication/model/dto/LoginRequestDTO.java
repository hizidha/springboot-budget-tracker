package com.devland.assignment.finalproject.authentication.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Username are required")
    private String username;

    @NotBlank(message = "Password are required")
    private String password;
}