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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlockUnblockAccountUseCase")
class BlockUnblockAccountUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @InjectMocks BlockUnblockAccountUseCaseImpl useCase;

    @Test
    @DisplayName("block deve bloquear conta ativa (RN008)")
    void blockShouldBlockActiveAccount() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).status(AccountStatus.ACTIVE).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenReturn(account);

        Account result = useCase.block(id, "Fraud detected");

        assertThat(result.getStatus()).isEqualTo(AccountStatus.BLOCKED);
    }

    @Test
    @DisplayName("block deve lancar excecao quando justificativa estiver ausente (RN008)")
    void blockShouldThrowWhenJustificationIsBlank() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.block(id, ""))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Justification");
    }

    @Test
    @DisplayName("block deve lancar excecao quando conta nao existir")
    void blockShouldThrowWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.block(id, "reason"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("block deve lancar excecao quando conta ja estiver bloqueada")
    void blockShouldThrowWhenAlreadyBlocked() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).status(AccountStatus.BLOCKED).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.block(id, "reason"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already blocked");
    }

    @Test
    @DisplayName("unblock deve desbloquear conta bloqueada (RN008)")
    void unblockShouldUnblockBlockedAccount() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).status(AccountStatus.BLOCKED).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenReturn(account);

        Account result = useCase.unblock(id, "Investigation complete");

        assertThat(result.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("unblock deve lancar excecao quando justificativa estiver ausente (RN008)")
    void unblockShouldThrowWhenJustificationIsBlank() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.unblock(id, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Justification");
    }

    @Test
    @DisplayName("unblock deve lancar excecao quando conta nao existir")
    void unblockShouldThrowWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.unblock(id, "reason"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("unblock deve lancar excecao quando conta nao estiver bloqueada")
    void unblockShouldThrowWhenNotBlocked() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).status(AccountStatus.ACTIVE).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> useCase.unblock(id, "reason"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not blocked");
    }
}
