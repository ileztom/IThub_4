package com.library.loan.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String LOAN_EXCHANGE = "library.loans.exchange";
    public static final String LOAN_QUEUE = "library.loans.queue";
    public static final String LOAN_ROUTING_KEY = "loan.events";

    @Bean
    public TopicExchange loanExchange() {
        return new TopicExchange(LOAN_EXCHANGE);
    }

    @Bean
    public Queue loanQueue() {
        return QueueBuilder.durable(LOAN_QUEUE).build();
    }

    @Bean
    public Binding loanBinding(Queue loanQueue, TopicExchange loanExchange) {
        return BindingBuilder.bind(loanQueue).to(loanExchange).with(LOAN_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
