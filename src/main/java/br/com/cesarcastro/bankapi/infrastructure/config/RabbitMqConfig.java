package br.com.cesarcastro.bankapi.infrastructure.config;

import br.com.cesarcastro.bankapi.infrastructure.constants.BankApiConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    Queue transactionQueue() {
        return QueueBuilder.durable(BankApiConstants.QUEUE_TRANSACTIONS)
                .withArgument("x-dead-letter-exchange", BankApiConstants.EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", BankApiConstants.ROUTING_KEY_DLQ)
                .build();
    }

    @Bean
    DirectExchange notificationsExchange() {
        return new DirectExchange(BankApiConstants.EXCHANGE_BANK_NOTIFICATIONS, true, false);
    }

    @Bean
    Binding transactionBinding(Queue transactionQueue, DirectExchange notificationsExchange) {
        return BindingBuilder.bind(transactionQueue)
                .to(notificationsExchange)
                .with(BankApiConstants.ROUTING_KEY_TRANSACTION);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(BankApiConstants.QUEUE_DLQ).build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(BankApiConstants.EXCHANGE_DLX, true, false);
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
