package br.com.cesarcastro.bankapi.adapter.in.web.customer.response;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import br.com.cesarcastro.bankapi.domain.model.DocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String document,
        DocumentType documentType,
        String email,
        String phone,
        CustomerStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getDocument(), c.getDocumentType(),
                c.getEmail(), c.getPhone(), c.getStatus(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
