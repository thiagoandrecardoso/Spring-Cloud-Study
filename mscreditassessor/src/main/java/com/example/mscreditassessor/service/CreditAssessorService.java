package com.example.mscreditassessor.service;

import com.example.mscreditassessor.domain.model.*;
import com.example.mscreditassessor.exceptions.ClientDataNotFoundException;
import com.example.mscreditassessor.exceptions.ErrorCommunicateMicroServicesException;
import com.example.mscreditassessor.exceptions.ErrorRequestCardIssueException;
import com.example.mscreditassessor.infra.clients.CardResourceClient;
import com.example.mscreditassessor.infra.clients.ClientResourceClient;
import com.example.mscreditassessor.infra.mqueue.RequestPublisherCard;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditAssessorService {

    private final ClientResourceClient clientResourceClient;
    private final CardResourceClient cardResourceClient;
    private final RequestPublisherCard requestPublisherCard;

    public ClientSituation getClientSituation(String cpf) throws ClientDataNotFoundException, ErrorCommunicateMicroServicesException {
        try {
            ResponseEntity<ClientData> clientDataResponse = clientResourceClient.getClient(cpf);
            ResponseEntity<List<ApprovedCard>> cardsResponse = cardResourceClient.getCardByClient(cpf);

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

    public CustomerFeedBack evaluate(String cpf, Long rent) throws ClientDataNotFoundException, ErrorCommunicateMicroServicesException {
        try {
            ResponseEntity<ClientData> clientDataResponseEntity = clientResourceClient.getClient(cpf);
            ResponseEntity<List<Card>> cardReponseEntity = cardResourceClient.getCardRevenueTo(rent);

            List<Card> cardList = cardReponseEntity.getBody();
            assert cardList != null;
            var listApprovedCard = cardList.stream().map(card -> {
                ClientData clientData = clientDataResponseEntity.getBody();
                BigDecimal limitBasic = card.getLimitRent();
                assert clientData != null;
                BigDecimal ageBigDecimal = BigDecimal.valueOf(clientData.getAge());
                var factor = ageBigDecimal.divide(BigDecimal.valueOf(10));
                BigDecimal limitApproved = factor.multiply(limitBasic);

                ApprovedCard approvedCard = new ApprovedCard();
                approvedCard.setName(card.getName());
                approvedCard.setFlagCard(card.getFlagCard());
                approvedCard.setLimitRent(limitApproved);
                return approvedCard;
            }).collect(Collectors.toList());

            return new CustomerFeedBack(listApprovedCard);
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new ClientDataNotFoundException();
            }
            throw new ErrorCommunicateMicroServicesException(e.getMessage(), status);
        }
    }

    public ProtocolRequestCard requestCardIssue(CardIssueData data) throws ErrorRequestCardIssueException {
        try {
            requestPublisherCard.requestCard(data);
            var protocol = UUID.randomUUID().toString();
            return new ProtocolRequestCard(protocol);
        } catch (Exception ex) {
            throw new ErrorRequestCardIssueException(ex.getMessage());
        }
    }
}
