package br.com.cesarcastro.bankapi.adapter.out.persistence.transaction;

import br.com.cesarcastro.bankapi.domain.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionPersistenceAdapter")
class TransactionPersistenceAdapterTest {

    @Mock
    TransactionJpaRepository jpaRepository;

    @InjectMocks
    TransactionPersistenceAdapter adapter;

    @Test
    @DisplayName("save deve persistir e retornar a transacao")
    void savePersistsTransaction() {
        Transaction tx = Transaction.builder().id(UUID.randomUUID()).build();
        when(jpaRepository.save(tx)).thenReturn(tx);

        Transaction result = adapter.save(tx);

        assertThat(result).isSameAs(tx);
    }

    @Test
    @DisplayName("loadById deve delegar ao repositorio")
    void loadByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        Transaction tx = Transaction.builder().id(id).build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(tx));

        Optional<Transaction> result = adapter.loadById(id);

        assertThat(result).contains(tx);
    }

    @Test
    @DisplayName("loadByAccountId deve retornar pagina filtrada por conta e periodo")
    void loadByAccountIdReturnsPaginatedResults() {
        UUID accountId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();
        Pageable pageable = Pageable.unpaged();
        Page<Transaction> page = new PageImpl<>(List.of(Transaction.builder().build()));
        when(jpaRepository.findByAccountIdAndDateRange(accountId, from, to, pageable)).thenReturn(page);

        Page<Transaction> result = adapter.loadByAccountId(accountId, from, to, pageable);

        assertThat(result).isSameAs(page);
    }
}
