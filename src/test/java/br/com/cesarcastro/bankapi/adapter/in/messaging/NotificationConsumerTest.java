package br.com.cesarcastro.bankapi.adapter.in.messaging;

import br.com.cesarcastro.bankapi.application.port.in.notification.ProcessNotificationUseCase;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationConsumer")
class NotificationConsumerTest {

    @Mock
    ProcessNotificationUseCase processNotificationUseCase;

    @InjectMocks
    NotificationConsumer consumer;

    @Test
    @DisplayName("consume deve delegar para processNotificationUseCase")
    void shouldDelegateToProcessNotificationUseCase() {
        // Arrange
        NotificationEvent event = new NotificationEvent(
                "user@bank.com",
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );

        // Act
        consumer.consume(event);

        // Assert
        verify(processNotificationUseCase).execute(event);
    }
}
