package br.com.cesarcastro.bankapi.application.port.out.account;

import java.util.UUID;

public interface DeleteAccountPort {
    void deleteById(UUID id);
    boolean hasZeroBalance(UUID accountId);
}
