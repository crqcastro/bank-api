package br.com.cesarcastro.bankapi.adapter.out.messaging;

import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;
import br.com.cesarcastro.bankapi.infrastructure.constants.BankApiConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitMqNotificationAdapter")
class RabbitMqNotificationAdapterTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    RabbitMqNotificationAdapter adapter;

    @Test
    @DisplayName("send deve publicar o evento no exchange correto com a routing key correta")
    void sendPublishesEventToCorrectExchange() {
        NotificationEvent event = new NotificationEvent(
                "user@bank.com",
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );

        adapter.send(event);

        verify(rabbitTemplate).convertAndSend(
                BankApiConstants.EXCHANGE_BANK_NOTIFICATIONS,
                BankApiConstants.ROUTING_KEY_TRANSACTION,
                event
        );
    }
}
