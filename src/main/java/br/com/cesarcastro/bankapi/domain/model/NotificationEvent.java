package br.com.cesarcastro.bankapi.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationEvent(
        String recipientEmail,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal newBalance,
        UUID accountId,
        OffsetDateTime timestamp
) {}
