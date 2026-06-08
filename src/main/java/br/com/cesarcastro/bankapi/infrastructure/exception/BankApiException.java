package br.com.cesarcastro.bankapi.infrastructure.exception;

public abstract class BankApiException extends RuntimeException {

    protected BankApiException(String message) {
        super(message);
    }
}
