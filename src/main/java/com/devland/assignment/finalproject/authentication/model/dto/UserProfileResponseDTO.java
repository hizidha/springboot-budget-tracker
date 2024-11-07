package com.devland.assignment.finalproject.authentication.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private String username;
    private String email;
    private String name;
    private BigDecimal balance;
}