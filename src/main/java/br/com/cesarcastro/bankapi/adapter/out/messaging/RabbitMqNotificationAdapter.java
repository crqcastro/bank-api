package br.com.cesarcastro.bankapi.adapter.out.messaging;

import br.com.cesarcastro.bankapi.application.port.out.notification.SendNotificationPort;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.infrastructure.constants.BankApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqNotificationAdapter implements SendNotificationPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(NotificationEvent event) {
        log.debug("Publishing notification event: type={}, account={}", event.transactionType(), event.accountId());
        rabbitTemplate.convertAndSend(
                BankApiConstants.EXCHANGE_BANK_NOTIFICATIONS,
                BankApiConstants.ROUTING_KEY_TRANSACTION,
                event
        );
    }
}
