package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.out.customer.DeleteCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCustomerUseCase")
class DeleteCustomerUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @Mock DeleteCustomerPort deleteCustomerPort;
    @InjectMocks DeleteCustomerUseCaseImpl useCase;

    @Test
    @DisplayName("deve excluir cliente sem contas (RN003)")
    void executeShouldDeleteCustomerWithNoAccounts() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        when(deleteCustomerPort.hasAccounts(id)).thenReturn(false);

        useCase.execute(id);

        verify(deleteCustomerPort).deleteById(id);
    }

    @Test
    @DisplayName("deve lancar excecao quando cliente nao existir")
    void executeShouldThrowWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando cliente possuir contas (RN003)")
    void executeShouldThrowWhenCustomerHasAccounts() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        when(deleteCustomerPort.hasAccounts(id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("accounts");
    }
}
