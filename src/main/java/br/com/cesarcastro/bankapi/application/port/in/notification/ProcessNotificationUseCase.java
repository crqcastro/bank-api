package br.com.cesarcastro.bankapi.application.port.in.notification;

import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;

public interface ProcessNotificationUseCase {
    void execute(NotificationEvent event);
}
