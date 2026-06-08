package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAccountUseCase")
class CreateAccountUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @Mock SaveAccountPort saveAccountPort;
    @InjectMocks CreateAccountUseCaseImpl useCase;

    @Test
    @DisplayName("deve criar conta quando o cliente esta ativo")
    void shouldCreateAccountForActiveCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).status(CustomerStatus.ACTIVE).build();
        when(loadCustomerPort.loadById(customerId)).thenReturn(Optional.of(customer));
        when(saveAccountPort.save(any())).thenReturn(Account.builder().customer(customer).build());

        useCase.execute(customerId);

        verify(saveAccountPort).save(any());
    }

    @Test
    @DisplayName("deve lancar excecao quando o cliente nao for encontrado")
    void shouldThrowWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando o cliente estiver inativo")
    void shouldThrowWhenCustomerIsInactive() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).status(CustomerStatus.INACTIVE).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("inactive");
    }
}
