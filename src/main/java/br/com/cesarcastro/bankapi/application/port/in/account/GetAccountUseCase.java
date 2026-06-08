package br.com.cesarcastro.bankapi.application.port.in.account;

import br.com.cesarcastro.bankapi.domain.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetAccountUseCase {
    Account execute(UUID id);
    Page<Account> executeByCustomer(UUID customerId, Pageable pageable);
}
