package br.com.cesarcastro.bankapi.application.port.in.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface WithdrawUseCase {
    BalanceResult execute(WithdrawCommand command);

    record WithdrawCommand(UUID accountId, BigDecimal amount, String description) {}

    record BalanceResult(UUID accountId, BigDecimal newBalance, UUID transactionId, OffsetDateTime timestamp) {}
}
