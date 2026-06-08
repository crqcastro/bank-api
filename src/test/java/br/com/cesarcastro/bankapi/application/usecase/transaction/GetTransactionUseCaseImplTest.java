package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.out.transaction.LoadTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTransactionUseCase")
class GetTransactionUseCaseImplTest {

    @Mock LoadTransactionPort loadTransactionPort;
    @InjectMocks GetTransactionUseCaseImpl useCase;

    @Test
    @DisplayName("deve retornar transacao pelo ID")
    void executeShouldReturnTransactionById() {
        UUID id = UUID.randomUUID();
        Transaction tx = Transaction.builder().id(id).build();
        when(loadTransactionPort.loadById(id)).thenReturn(Optional.of(tx));

        assertThat(useCase.execute(id)).isSameAs(tx);
    }

    @Test
    @DisplayName("deve lancar excecao quando transacao nao existir")
    void executeShouldThrowWhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        when(loadTransactionPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve retornar pagina de transacoes por conta")
    void executeByAccountShouldReturnPage() {
        UUID accountId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();
        Pageable pageable = Pageable.unpaged();
        Page<Transaction> page = new PageImpl<>(List.of(Transaction.builder().build()));
        when(loadTransactionPort.loadByAccountId(accountId, from, to, pageable)).thenReturn(page);

        assertThat(useCase.executeByAccount(accountId, from, to, pageable)).isSameAs(page);
    }
}
