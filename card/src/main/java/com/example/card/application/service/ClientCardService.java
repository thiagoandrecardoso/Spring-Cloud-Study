package com.example.card.application.service;

import com.example.card.domain.ClientCard;
import com.example.card.infra.repository.ClientCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCardService {
    private final ClientCardRepository cardRepository;

    public List<ClientCard> listCardByCpf(String cpf){
        return cardRepository.findByCpf(cpf);
    }
}
