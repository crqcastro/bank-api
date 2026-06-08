package br.com.cesarcastro.bankapi.application.usecase.notification;

import br.com.cesarcastro.bankapi.application.port.in.notification.ProcessNotificationUseCase;
import br.com.cesarcastro.bankapi.application.port.out.email.SendEmailPort;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessNotificationUseCaseImpl implements ProcessNotificationUseCase {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final SendEmailPort sendEmailPort;

    @Override
    @Observed(name = "usecase.notification.process")
    public void execute(NotificationEvent event) {
        log.info("Processing notification: type={}, recipient={}", event.transactionType(), event.recipientEmail());
        sendEmailPort.send(
                event.recipientEmail(),
                buildSubject(event),
                buildBody(event)
        );
    }

    private String buildSubject(NotificationEvent event) {
        String amount = formatAmount(event.amount());
        return switch (event.transactionType()) {
            case DEPOSIT -> "[Bank API] Deposito de R$ " + amount + " recebido";
            case WITHDRAWAL -> "[Bank API] Saque de R$ " + amount + " realizado";
            case TRANSFER_OUT -> "[Bank API] Transferencia de R$ " + amount + " enviada";
            case TRANSFER_IN -> "[Bank API] Transferencia de R$ " + amount + " recebida";
        };
    }

    private String buildBody(NotificationEvent event) {
        String title = switch (event.transactionType()) {
            case DEPOSIT -> "Deposito Recebido";
            case WITHDRAWAL -> "Saque Realizado";
            case TRANSFER_OUT -> "Transferencia Enviada";
            case TRANSFER_IN -> "Transferencia Recebida";
        };
        String typeLabel = switch (event.transactionType()) {
            case DEPOSIT -> "Deposito";
            case WITHDRAWAL -> "Saque";
            case TRANSFER_OUT -> "Transferencia enviada";
            case TRANSFER_IN -> "Transferencia recebida";
        };
        return "<html><body style='font-family:Arial,sans-serif;color:#333;max-width:600px;margin:0 auto'>"
                + "<div style='background:#1a73e8;padding:24px;border-radius:8px 8px 0 0'>"
                + "<h2 style='color:#fff;margin:0'>Bank API</h2></div>"
                + "<div style='border:1px solid #e0e0e0;border-top:none;padding:32px;border-radius:0 0 8px 8px'>"
                + "<h3 style='margin-top:0'>" + title + "</h3>"
                + "<table style='width:100%;border-collapse:collapse'>"
                + row("Tipo", typeLabel)
                + row("Valor", "R$ " + formatAmount(event.amount()))
                + row("Saldo atual", "R$ " + formatAmount(event.newBalance()))
                + row("Conta", event.accountId().toString())
                + row("Data/Hora", event.timestamp().format(FORMATTER))
                + "</table>"
                + "<p style='margin-top:24px;font-size:12px;color:#888'>"
                + "Esta e uma mensagem automatica. Nao responda este e-mail.</p>"
                + "</div></body></html>";
    }

    private String row(String label, String value) {
        return "<tr><td style='padding:8px 0;border-bottom:1px solid #f0f0f0;"
                + "font-weight:bold;width:140px'>" + label + "</td>"
                + "<td style='padding:8px 0;border-bottom:1px solid #f0f0f0'>" + value + "</td></tr>";
    }

    private String formatAmount(BigDecimal amount) {
        return String.format("%.2f", amount).replace(".", ",");
    }
}
