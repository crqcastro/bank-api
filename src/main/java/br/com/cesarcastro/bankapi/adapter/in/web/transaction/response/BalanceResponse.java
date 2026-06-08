package br.com.cesarcastro.bankapi.adapter.in.web.transaction.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BalanceResponse(
        UUID accountId,
        BigDecimal newBalance,
        UUID transactionId,
        OffsetDateTime timestamp
) {}
