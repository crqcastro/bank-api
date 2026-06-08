package br.com.cesarcastro.bankapi.adapter.in.web.transaction.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferReceiptResponse(
        UUID originAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        UUID originTransactionId,
        UUID destinationTransactionId,
        OffsetDateTime timestamp
) {}
