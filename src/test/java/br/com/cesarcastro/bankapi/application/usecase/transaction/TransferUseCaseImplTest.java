package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.TransferUseCase.TransferCommand;
import br.com.cesarcastro.bankapi.application.port.in.transaction.TransferUseCase.TransferResult;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.notification.SendNotificationPort;
import br.com.cesarcastro.bankapi.application.port.out.transaction.SaveTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferUseCase")
class TransferUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @Mock SaveTransactionPort saveTransactionPort;
    @Mock SendNotificationPort sendNotificationPort;
    @InjectMocks TransferUseCaseImpl useCase;

    private Account activeAccount(UUID id, BigDecimal balance) {
        Customer customer = Customer.builder().email("test@bank.com").build();
        return Account.builder().id(id).customer(customer)
                .status(AccountStatus.ACTIVE).balance(balance).version(0L).build();
    }

    @Test
    @DisplayName("deve transferir entre duas contas e gerar dois registros de transacao (RN012)")
    void shouldTransferBetweenAccounts() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        Account origin = activeAccount(originId, BigDecimal.valueOf(300));
        Account dest = activeAccount(destId, BigDecimal.valueOf(50));
        Transaction tx = Transaction.builder().id(UUID.randomUUID()).createdAt(OffsetDateTime.now()).build();
        when(loadAccountPort.loadByIdForUpdate(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            return Optional.of(id.equals(originId) ? origin : dest);
        });
        when(saveAccountPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(saveTransactionPort.save(any())).thenReturn(tx);

        TransferResult result = useCase.execute(new TransferCommand(originId, destId, BigDecimal.valueOf(100), null));

        assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        verify(sendNotificationPort, times(2)).send(any());
    }


    @Test
    @DisplayName("deve lancar excecao quando o valor for zero ou negativo")
    void shouldThrowWhenAmountIsZeroOrNegative() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        assertThatThrownBy(() -> useCase.execute(new TransferCommand(originId, destId, BigDecimal.ZERO, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("greater than zero");
    }
    @Test
    @DisplayName("deve lancar excecao quando a conta de origem e destino forem iguais")
    void shouldThrowWhenSameAccount() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> useCase.execute(new TransferCommand(id, id, BigDecimal.ONE, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("different");
    }

    @Test
    @DisplayName("deve lancar excecao quando o saldo da conta de origem for insuficiente (RN012)")
    void shouldThrowWhenInsufficientBalance() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        Account origin = activeAccount(originId, BigDecimal.valueOf(10));
        Account dest = activeAccount(destId, BigDecimal.ZERO);
        when(loadAccountPort.loadByIdForUpdate(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            return Optional.of(id.equals(originId) ? origin : dest);
        });
        assertThatThrownBy(() -> useCase.execute(new TransferCommand(originId, destId, BigDecimal.valueOf(100), null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("Insufficient balance");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta de origem estiver bloqueada (RN012)")
    void shouldThrowWhenOriginIsBlocked() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        Account origin = activeAccount(originId, BigDecimal.valueOf(300));
        origin.setStatus(AccountStatus.BLOCKED);
        Account dest = activeAccount(destId, BigDecimal.ZERO);
        when(loadAccountPort.loadByIdForUpdate(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            return Optional.of(id.equals(originId) ? origin : dest);
        });
        assertThatThrownBy(() -> useCase.execute(new TransferCommand(originId, destId, BigDecimal.valueOf(100), null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("blocked");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta de origem estiver inativa (RN012)")
    void shouldThrowWhenOriginIsInactive() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        Account origin = activeAccount(originId, BigDecimal.valueOf(300));
        origin.setStatus(AccountStatus.INACTIVE);
        Account dest = activeAccount(destId, BigDecimal.ZERO);
        when(loadAccountPort.loadByIdForUpdate(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            return Optional.of(id.equals(originId) ? origin : dest);
        });
        assertThatThrownBy(() -> useCase.execute(new TransferCommand(originId, destId, BigDecimal.valueOf(100), null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("not active");
    }
}
