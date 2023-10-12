package com.example.card.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private FlagCard flagCard;
    private BigDecimal rent;
    private BigDecimal limitRent;

    public Card(String name, FlagCard flagCard, BigDecimal rent, BigDecimal limit) {
        this.name = name;
        this.flagCard = flagCard;
        this.rent = rent;
        this.limitRent = limit;
    }
}
