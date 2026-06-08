package br.com.cesarcastro.bankapi.application.port.out.transaction;

import br.com.cesarcastro.bankapi.domain.model.Transaction;

public interface SaveTransactionPort {
    Transaction save(Transaction transaction);
}
