package br.com.cesarcastro.bankapi.application.port.out.notification;

import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;

public interface SendNotificationPort {
    void send(NotificationEvent event);
}
