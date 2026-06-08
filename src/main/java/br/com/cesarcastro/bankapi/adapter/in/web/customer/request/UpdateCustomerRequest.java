package br.com.cesarcastro.bankapi.adapter.in.web.customer.request;

import jakarta.validation.constraints.Email;

public record UpdateCustomerRequest(
        String name,
        String nameChangeJustification,
        @Email(message = "Email must be valid") String email,
        String phone
) {}
