package br.com.cesarcastro.bankapi.application.port.out.transaction;

import br.com.cesarcastro.bankapi.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface LoadTransactionPort {
    Optional<Transaction> loadById(UUID id);
    Page<Transaction> loadByAccountId(UUID accountId, OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}
