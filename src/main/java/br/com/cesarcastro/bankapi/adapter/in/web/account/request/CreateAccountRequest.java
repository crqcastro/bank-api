package br.com.cesarcastro.bankapi.adapter.in.web.account.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(
        @NotNull(message = "Customer ID is required") UUID customerId
) {}
