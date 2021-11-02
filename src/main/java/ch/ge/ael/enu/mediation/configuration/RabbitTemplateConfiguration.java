package ch.ge.ael.enu.mediation.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTemplateConfiguration {

    @Value("${app.rabbitmq.inverse.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.dlq.exchange}")
    private String deadLetterExchange;

    @Bean("defaultTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchangeName);
        return rabbitTemplate;
    }

    @Bean("dlxTemplate")
    public RabbitTemplate rabbitDLXTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(deadLetterExchange);
        return rabbitTemplate;
    }
}
