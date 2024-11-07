package com.devland.assignment.finalproject.financialgoal.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.financialgoal.model.dto.FinancialGoalResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal goalAmount;

    private Double percentage;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public FinancialGoalResponseDTO convertToResponse() {
        return FinancialGoalResponseDTO.builder()
                .id(this.id)
                .name(this.name)
                .goalAmount(this.goalAmount)
                .percentage(this.percentage)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}