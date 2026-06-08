package br.com.cesarcastro.bankapi.application.port.in.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface DepositUseCase {
    BalanceResult execute(DepositCommand command);

    record DepositCommand(UUID accountId, BigDecimal amount, String description) {}

    record BalanceResult(UUID accountId, BigDecimal newBalance, UUID transactionId, OffsetDateTime timestamp) {}
}
