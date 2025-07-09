package com.emily.freelance.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "wordCountQueue";

    @Bean
    public Queue wordCountQueue() {
        return new Queue(QUEUE_NAME);
    }
}
