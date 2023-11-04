package com.example.mscreditassessor.infra.mqueue;

import com.example.mscreditassessor.domain.model.CardIssueData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestPublisherCard {
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public void requestCard(CardIssueData cardIssueData) throws JsonProcessingException{
        var json = convertIntoJson(cardIssueData);
        rabbitTemplate.convertAndSend(queue.getName(), json);
    }

    public String convertIntoJson(CardIssueData cardIssueData) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(cardIssueData);
    }
}
