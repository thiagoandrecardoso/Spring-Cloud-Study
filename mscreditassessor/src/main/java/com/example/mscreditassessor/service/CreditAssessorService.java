package com.example.mscreditassessor.service;

import com.example.mscreditassessor.domain.model.CardPerClient;
import com.example.mscreditassessor.domain.model.ClientData;
import com.example.mscreditassessor.domain.model.ClientSituation;
import com.example.mscreditassessor.infra.clients.CardResourceClient;
import com.example.mscreditassessor.infra.clients.ClientResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditAssessorService {

    private final ClientResourceClient clientResourceClient;
    private final CardResourceClient cardResourceClient;

    public ClientSituation getClientSituation(String cpf) {
        ResponseEntity<ClientData> clientDataResponse = clientResourceClient.getClient(cpf);
        ResponseEntity<List<CardPerClient>> cardsResponse = cardResourceClient.getCardByClient(cpf);

        return ClientSituation.builder()
                .clientData(clientDataResponse.getBody())
                .cardPerClientList(cardsResponse.getBody())
                .build();
    }
}
