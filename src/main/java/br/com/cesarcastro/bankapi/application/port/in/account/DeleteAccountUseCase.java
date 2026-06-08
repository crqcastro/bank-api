package br.com.cesarcastro.bankapi.application.port.in.account;

import java.util.UUID;

public interface DeleteAccountUseCase {
    void execute(UUID accountId);
}
