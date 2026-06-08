package br.com.cesarcastro.bankapi.infrastructure.exception;

import java.util.UUID;

public class ResourceNotFoundException extends BankApiException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
