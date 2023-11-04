package com.example.mscreditassessor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    @Value("${mq.queues.issuing-cards}")
    private String issueCardQueue;

    @Bean
    public Queue queueIssueCards(){
        return new Queue(issueCardQueue, true);
    }
}
