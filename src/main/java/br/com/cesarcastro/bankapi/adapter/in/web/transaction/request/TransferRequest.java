package br.com.cesarcastro.bankapi.adapter.in.web.transaction.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "Origin account ID is required") UUID originAccountId,
        @NotNull(message = "Destination account ID is required") UUID destinationAccountId,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        String description
) {}
