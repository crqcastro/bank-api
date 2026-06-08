package br.com.cesarcastro.bankapi.application.port.out.account;

import br.com.cesarcastro.bankapi.domain.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface LoadAccountPort {
    Optional<Account> loadById(UUID id);
    Optional<Account> loadByIdForUpdate(UUID id);
    Page<Account> loadByCustomerId(UUID customerId, Pageable pageable);
}
