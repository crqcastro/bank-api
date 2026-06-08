package br.com.cesarcastro.bankapi.application.port.in.account;

import br.com.cesarcastro.bankapi.domain.model.Account;

import java.util.UUID;

public interface InactivateAccountUseCase {
    Account execute(UUID accountId);
}
