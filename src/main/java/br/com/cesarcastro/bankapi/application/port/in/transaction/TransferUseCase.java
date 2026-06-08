package br.com.cesarcastro.bankapi.application.port.in.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface TransferUseCase {
    TransferResult execute(TransferCommand command);

    record TransferCommand(UUID originAccountId, UUID destinationAccountId, BigDecimal amount, String description) {}

    record TransferResult(
            UUID originAccountId,
            UUID destinationAccountId,
            BigDecimal amount,
            UUID originTransactionId,
            UUID destinationTransactionId,
            OffsetDateTime timestamp
    ) {}
}
