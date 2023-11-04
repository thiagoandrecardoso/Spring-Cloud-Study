package com.example.card.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardIssueData {
    private Long idCard;
    private String cpf;
    private String address;
    private BigDecimal limitFree;
}
