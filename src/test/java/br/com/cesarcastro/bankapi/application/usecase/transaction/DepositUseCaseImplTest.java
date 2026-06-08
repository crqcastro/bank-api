package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.DepositUseCase.BalanceResult;
import br.com.cesarcastro.bankapi.application.port.in.transaction.DepositUseCase.DepositCommand;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.notification.SendNotificationPort;
import br.com.cesarcastro.bankapi.application.port.out.transaction.SaveTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
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
@DisplayName("DepositUseCase")
class DepositUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @Mock SaveTransactionPort saveTransactionPort;
    @Mock SendNotificationPort sendNotificationPort;
    @InjectMocks DepositUseCaseImpl useCase;

    private Account activeAccount() {
        Customer customer = Customer.builder().email("test@bank.com").build();
        return Account.builder().id(UUID.randomUUID()).customer(customer)
                .status(AccountStatus.ACTIVE).balance(BigDecimal.valueOf(100)).build();
    }

    @Test
    @DisplayName("deve creditar o valor e retornar o novo saldo (RN010)")
    void shouldDepositAndReturnNewBalance() {
        Account account = activeAccount();
        Transaction tx = Transaction.builder().id(UUID.randomUUID()).createdAt(OffsetDateTime.now()).build();
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenReturn(account);
        when(saveTransactionPort.save(any())).thenReturn(tx);

        BalanceResult result = useCase.execute(new DepositCommand(account.getId(), BigDecimal.valueOf(50), "test"));

        assertThat(result.newBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
        verify(sendNotificationPort).send(any());
    }

    @Test
    @DisplayName("deve lancar excecao quando o valor for zero ou negativo")
    void shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> useCase.execute(new DepositCommand(UUID.randomUUID(), BigDecimal.ZERO, null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta nao for encontrada")
    void shouldThrowWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(new DepositCommand(id, BigDecimal.ONE, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta estiver inativa")
    void shouldThrowWhenAccountIsInactive() {
        Account account = activeAccount();
        account.setStatus(AccountStatus.INACTIVE);
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> useCase.execute(new DepositCommand(account.getId(), BigDecimal.ONE, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("not active");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta estiver bloqueada")
    void shouldThrowWhenAccountIsBlocked() {
        Account account = activeAccount();
        account.setStatus(AccountStatus.BLOCKED);
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> useCase.execute(new DepositCommand(account.getId(), BigDecimal.ONE, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("blocked");
    }
}
