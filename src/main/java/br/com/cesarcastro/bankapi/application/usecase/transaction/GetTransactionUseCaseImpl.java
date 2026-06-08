package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.GetTransactionUseCase;
import br.com.cesarcastro.bankapi.application.port.out.transaction.LoadTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionUseCaseImpl implements GetTransactionUseCase {

    private final LoadTransactionPort loadTransactionPort;

    @Override
    @Transactional(readOnly = true)
    public Transaction execute(UUID id) {
        return loadTransactionPort.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> executeByAccount(UUID accountId, OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        return loadTransactionPort.loadByAccountId(accountId, from, to, pageable);
    }
}
