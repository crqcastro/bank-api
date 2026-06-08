package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeactivateCustomerUseCase")
class DeactivateCustomerUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @Mock SaveCustomerPort saveCustomerPort;
    @InjectMocks DeactivateCustomerUseCaseImpl useCase;

    @Test
    @DisplayName("deve desativar cliente ativo sem contas ativas")
    void shouldDeactivateActiveCustomerWithNoAccounts() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).status(CustomerStatus.ACTIVE).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        when(loadCustomerPort.hasActiveAccounts(id)).thenReturn(false);

        useCase.execute(id);

        verify(saveCustomerPort).save(customer);
    }

    @Test
    @DisplayName("deve lancar excecao quando o cliente nao for encontrado")
    void shouldThrowWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando o cliente ja estiver inativo (RN002)")
    void shouldThrowWhenCustomerAlreadyInactive() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).status(CustomerStatus.INACTIVE).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already inactive");
    }

    @Test
    @DisplayName("deve lancar excecao quando o cliente possuir contas ativas (RN002)")
    void shouldThrowWhenCustomerHasActiveAccounts() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).status(CustomerStatus.ACTIVE).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        when(loadCustomerPort.hasActiveAccounts(id)).thenReturn(true);
        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("active accounts");
    }
}
