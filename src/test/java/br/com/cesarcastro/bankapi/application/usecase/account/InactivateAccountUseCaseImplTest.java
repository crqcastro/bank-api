package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InactivateAccountUseCase")
class InactivateAccountUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @InjectMocks InactivateAccountUseCaseImpl useCase;

    @Test
    @DisplayName("deve inativar conta ativa com saldo zero (RN006)")
    void executeShouldInactivateAccountWithZeroBalance() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id)
                .status(AccountStatus.ACTIVE).balance(BigDecimal.ZERO).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenReturn(account);

        Account result = useCase.execute(id);

        assertThat(result.getStatus()).isEqualTo(AccountStatus.INACTIVE);
    }

    @Test
    @DisplayName("deve lancar excecao quando conta nao existir")
    void executeShouldThrowWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando conta nao estiver ativa (RN006)")
    void executeShouldThrowWhenAccountIsNotActive() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id)
                .status(AccountStatus.INACTIVE).balance(BigDecimal.ZERO).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not active");
    }

    @Test
    @DisplayName("deve lancar excecao quando saldo nao for zero (RN006)")
    void executeShouldThrowWhenBalanceIsNotZero() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id)
                .status(AccountStatus.ACTIVE).balance(BigDecimal.valueOf(100)).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zero");
    }
}
