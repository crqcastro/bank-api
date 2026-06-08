package br.com.cesarcastro.bankapi.adapter.out.persistence.transaction;

import br.com.cesarcastro.bankapi.application.port.out.transaction.LoadTransactionPort;
import br.com.cesarcastro.bankapi.application.port.out.transaction.SaveTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements SaveTransactionPort, LoadTransactionPort {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> loadById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Transaction> loadByAccountId(UUID accountId, OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        if (from != null && to != null) {
            return jpaRepository.findByAccountIdAndDateRange(accountId, from, to, pageable);
        }
        if (from != null) {
            return jpaRepository.findByAccountIdFrom(accountId, from, pageable);
        }
        if (to != null) {
            return jpaRepository.findByAccountIdTo(accountId, to, pageable);
        }
        return jpaRepository.findByAccountId(accountId, pageable);
    }
}
