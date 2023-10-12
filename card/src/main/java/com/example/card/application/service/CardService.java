package com.example.card.application.service;

import com.example.card.domain.Card;
import com.example.card.infra.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public Card save(Card card){
        return cardRepository.save(card);
    }

    public List<Card> getCardsWithRentLessThanOrEqualTo(Long rent){
        BigDecimal rentBigDecimal = BigDecimal.valueOf(rent);
        return cardRepository.findByRentLessThanEqual(rentBigDecimal);
    }
}
