package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.out.account.DeleteAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteAccountUseCase")
class DeleteAccountUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock DeleteAccountPort deleteAccountPort;
    @InjectMocks DeleteAccountUseCaseImpl useCase;

    @Test
    @DisplayName("deve excluir conta com saldo zero (RN007)")
    void executeShouldDeleteAccountWithZeroBalance() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id)
                .status(AccountStatus.INACTIVE).balance(BigDecimal.ZERO).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        useCase.execute(id);

        verify(deleteAccountPort).deleteById(id);
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
    @DisplayName("deve lancar excecao quando saldo nao for zero (RN007)")
    void executeShouldThrowWhenBalanceIsNotZero() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id)
                .status(AccountStatus.ACTIVE).balance(BigDecimal.valueOf(50)).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zero");
    }
}
