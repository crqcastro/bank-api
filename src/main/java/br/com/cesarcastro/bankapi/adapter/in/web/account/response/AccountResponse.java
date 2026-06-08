package br.com.cesarcastro.bankapi.adapter.in.web.account.response;

import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID customerId,
        AccountStatus status,
        BigDecimal balance,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static AccountResponse from(Account a) {
        return new AccountResponse(a.getId(), a.getCustomer().getId(), a.getStatus(),
                a.getBalance(), a.getCreatedAt(), a.getUpdatedAt());
    }
}
