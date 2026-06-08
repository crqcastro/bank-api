package br.com.cesarcastro.bankapi.adapter.in.web.customer.request;

import br.com.cesarcastro.bankapi.domain.model.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Document is required") String document,
        @NotNull(message = "Document type is required") DocumentType documentType,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        String phone
) {}
