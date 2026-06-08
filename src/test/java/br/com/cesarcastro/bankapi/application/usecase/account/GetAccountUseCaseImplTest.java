package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAccountUseCase")
class GetAccountUseCaseImplTest {

    @Mock LoadAccountPort loadAccountPort;
    @InjectMocks GetAccountUseCaseImpl useCase;

    @Test
    @DisplayName("deve retornar conta pelo ID")
    void executeShouldReturnAccountById() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).build();
        when(loadAccountPort.loadById(id)).thenReturn(Optional.of(account));

        assertThat(useCase.execute(id)).isSameAs(account);
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
    @DisplayName("deve retornar pagina de contas por cliente")
    void executeByCustomerShouldReturnPage() {
        UUID customerId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<Account> page = new PageImpl<>(List.of(Account.builder().build()));
        when(loadAccountPort.loadByCustomerId(customerId, pageable)).thenReturn(page);

        assertThat(useCase.executeByCustomer(customerId, pageable)).isSameAs(page);
    }
}
