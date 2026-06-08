package br.com.cesarcastro.bankapi.adapter.in.messaging;

import br.com.cesarcastro.bankapi.application.port.in.notification.ProcessNotificationUseCase;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.infrastructure.constants.BankApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ProcessNotificationUseCase processNotificationUseCase;

    @RabbitListener(queues = BankApiConstants.QUEUE_TRANSACTIONS)
    public void consume(NotificationEvent event) {
        log.info("Received notification event: type={}, recipient={}", event.transactionType(), event.recipientEmail());
        processNotificationUseCase.execute(event);
    }
}
