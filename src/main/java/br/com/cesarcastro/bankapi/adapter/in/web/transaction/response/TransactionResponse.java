package br.com.cesarcastro.bankapi.adapter.in.web.transaction.response;

import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        String description,
        OffsetDateTime createdAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t.getId(), t.getAccount().getId(),
                t.getType(), t.getAmount(), t.getDescription(), t.getCreatedAt());
    }
}
