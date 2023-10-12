package com.example.card.application.dto;

import com.example.card.domain.ClientCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCardResponse {
    private String name;
    private String flag;
    private BigDecimal freeLimit;

    public static ClientCardResponse fromModel(ClientCard model) {
        return new ClientCardResponse(model.getCard().getName(),
                model.getCard().getFlagCard().toString(),
                model.getLimitRent());
    }
}
