package com.example.mscreditassessor.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApprovedCard {
    private String name;
    private String flagCard;
    private BigDecimal limitRent;
}
