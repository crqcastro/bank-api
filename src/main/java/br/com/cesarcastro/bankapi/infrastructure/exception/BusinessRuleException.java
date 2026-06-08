package br.com.cesarcastro.bankapi.infrastructure.exception;

public class BusinessRuleException extends BankApiException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
