package com.example.card.application.dto;

import com.example.card.domain.Card;
import com.example.card.domain.FlagCard;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardSaveRequest {
    private String name;
    private FlagCard flag;
    private BigDecimal rent;
    private BigDecimal limit;

    public Card toModel(){
        return new Card(name, flag, rent, limit);
    }
}
