package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.WithdrawUseCase.BalanceResult;
import br.com.cesarcastro.bankapi.application.port.in.transaction.WithdrawUseCase.WithdrawCommand;
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
@DisplayName("WithdrawUseCase")
class WithdrawUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @Mock SaveTransactionPort saveTransactionPort;
    @Mock SendNotificationPort sendNotificationPort;
    @InjectMocks WithdrawUseCaseImpl useCase;

    private Account activeAccount(BigDecimal balance) {
        Customer customer = Customer.builder().email("test@bank.com").build();
        return Account.builder().id(UUID.randomUUID()).customer(customer)
                .status(AccountStatus.ACTIVE).balance(balance).build();
    }

    @Test
    @DisplayName("deve debitar o valor e retornar o novo saldo (RN011)")
    void shouldWithdrawAndReturnNewBalance() {
        Account account = activeAccount(BigDecimal.valueOf(200));
        Transaction tx = Transaction.builder().id(UUID.randomUUID()).createdAt(OffsetDateTime.now()).build();
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenReturn(account);
        when(saveTransactionPort.save(any())).thenReturn(tx);

        BalanceResult result = useCase.execute(new WithdrawCommand(account.getId(), BigDecimal.valueOf(50), null));

        assertThat(result.newBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
        verify(sendNotificationPort).send(any());
    }

    @Test
    @DisplayName("deve lancar excecao quando o saldo for insuficiente (RN011)")
    void shouldThrowWhenInsufficientBalance() {
        Account account = activeAccount(BigDecimal.valueOf(10));
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> useCase.execute(new WithdrawCommand(account.getId(), BigDecimal.valueOf(50), null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("Insufficient balance");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta nao for encontrada")
    void shouldThrowWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(new WithdrawCommand(id, BigDecimal.ONE, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando o valor for zero ou negativo")
    void shouldThrowWhenAmountIsZero() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> useCase.execute(new WithdrawCommand(id, BigDecimal.ZERO, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta estiver bloqueada")
    void shouldThrowWhenAccountIsBlocked() {
        Account account = activeAccount(BigDecimal.valueOf(200));
        account.setStatus(AccountStatus.BLOCKED);
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.execute(new WithdrawCommand(account.getId(), BigDecimal.ONE, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("blocked");
    }

    @Test
    @DisplayName("deve lancar excecao quando a conta nao estiver ativa")
    void shouldThrowWhenAccountIsInactive() {
        Account account = activeAccount(BigDecimal.valueOf(200));
        account.setStatus(AccountStatus.INACTIVE);
        when(loadAccountPort.loadById(account.getId())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.execute(new WithdrawCommand(account.getId(), BigDecimal.ONE, null)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("not active");
    }
}
