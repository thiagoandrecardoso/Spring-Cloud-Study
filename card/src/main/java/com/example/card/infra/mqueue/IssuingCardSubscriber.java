package com.example.card.infra.mqueue;

import com.example.card.domain.Card;
import com.example.card.domain.CardIssueData;
import com.example.card.domain.ClientCard;
import com.example.card.infra.repository.CardRepository;
import com.example.card.infra.repository.ClientCardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IssuingCardSubscriber {

    private final CardRepository cardRepository;
    private final ClientCardRepository clientCardRepository;

    @RabbitListener(queues = "${mq.queues.issuing-cards}")
    public void receivingCardIssueRequest(@Payload String payload){
        var mapper = new ObjectMapper();
        try {
            CardIssueData data = mapper.readValue(payload, CardIssueData.class);
            Card card = cardRepository.findById(data.getIdCard()).orElseThrow();

            ClientCard clientCard = new ClientCard();
            clientCard.setCard(card);
            clientCard.setCpf(data.getCpf());
            clientCard.setLimitRent(data.getLimitFree());
            clientCardRepository.save(clientCard);
        } catch (Exception e) {
            log.error("Error on receiving card issue request {}", e.getMessage());
        }
    }
}
