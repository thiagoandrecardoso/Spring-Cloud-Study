package com.example.card.infra.mqueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class IssuingCardSubscriber {

    @RabbitListener(queues = "${mq.queues.issuing-cards}")
    public void receivingCardIssueRequest(@Payload String payload){
        System.out.println(payload);
    }
}
