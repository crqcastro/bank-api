package br.com.cesarcastro.bankapi.application.port.in.transaction;

import br.com.cesarcastro.bankapi.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface GetTransactionUseCase {
    Transaction execute(UUID id);
    Page<Transaction> executeByAccount(UUID accountId, OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}
