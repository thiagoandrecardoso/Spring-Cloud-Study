package com.example.mscreditassessor.service;

import com.example.mscreditassessor.domain.model.CardPerClient;
import com.example.mscreditassessor.domain.model.ClientData;
import com.example.mscreditassessor.domain.model.ClientSituation;
import com.example.mscreditassessor.exceptions.ClientDataNotFoundException;
import com.example.mscreditassessor.exceptions.ErrorCommunicateMicroServicesException;
import com.example.mscreditassessor.infra.clients.CardResourceClient;
import com.example.mscreditassessor.infra.clients.ClientResourceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditAssessorService {

    private final ClientResourceClient clientResourceClient;
    private final CardResourceClient cardResourceClient;

    public ClientSituation getClientSituation(String cpf) throws ClientDataNotFoundException, ErrorCommunicateMicroServicesException {
        try {
            ResponseEntity<ClientData> clientDataResponse = clientResourceClient.getClient(cpf);
            ResponseEntity<List<CardPerClient>> cardsResponse = cardResourceClient.getCardByClient(cpf);

            return ClientSituation.builder()
                    .clientData(clientDataResponse.getBody())
                    .cardPerClientList(cardsResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            if (HttpStatus.NOT_FOUND.value() == e.status()) {
                throw new ClientDataNotFoundException();
            }
            throw new ErrorCommunicateMicroServicesException(e.getMessage(), e.status());
        }

    }
}
