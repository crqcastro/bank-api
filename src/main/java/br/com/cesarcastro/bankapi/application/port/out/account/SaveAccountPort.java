package br.com.cesarcastro.bankapi.application.port.out.account;

import br.com.cesarcastro.bankapi.domain.model.Account;

public interface SaveAccountPort {
    Account save(Account account);
}
