package br.com.cesarcastro.bankapi.infrastructure.exception;

public class DuplicateResourceException extends BankApiException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
