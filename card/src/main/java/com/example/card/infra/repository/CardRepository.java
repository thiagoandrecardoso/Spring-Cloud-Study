package com.example.card.infra.repository;

import com.example.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByRentLessThanEqual(BigDecimal rentBigDecimal);
}
