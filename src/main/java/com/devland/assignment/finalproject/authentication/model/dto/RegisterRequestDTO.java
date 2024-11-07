package com.devland.assignment.finalproject.authentication.model.dto;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "Name are required")
    private String name;

    @NotBlank(message = "Email are required")
    private String email;

    @NotBlank(message = "Username are required")
    private String username;

    @NotBlank(message = "Password are required")
    private String password;

    public ApplicationUser convertToEntity() {
        BigDecimal currentBalance = BigDecimal.valueOf(0);

        return ApplicationUser.builder()
                .name(this.name)
                .email(this.email)
                .username(this.username)
                .password(this.password)
                .totalBalance(currentBalance)
                .build();
    }
}