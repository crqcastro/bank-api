package br.com.cesarcastro.bankapi.adapter.in.web.account.request;

import jakarta.validation.constraints.NotBlank;

public record BlockAccountRequest(
        @NotBlank(message = "Justification is required") String justification
) {}
