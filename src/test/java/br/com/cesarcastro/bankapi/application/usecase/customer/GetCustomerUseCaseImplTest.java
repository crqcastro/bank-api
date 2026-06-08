package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
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
@DisplayName("GetCustomerUseCase")
class GetCustomerUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @InjectMocks GetCustomerUseCaseImpl useCase;

    @Test
    @DisplayName("deve retornar cliente pelo ID")
    void executeShouldReturnCustomerById() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));

        assertThat(useCase.execute(id)).isSameAs(customer);
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
    @DisplayName("deve retornar pagina de clientes")
    void executeAllShouldReturnPage() {
        Pageable pageable = Pageable.unpaged();
        Page<Customer> page = new PageImpl<>(List.of(Customer.builder().build()));
        when(loadCustomerPort.loadAll(CustomerStatus.ACTIVE, pageable)).thenReturn(page);

        assertThat(useCase.executeAll(CustomerStatus.ACTIVE, pageable)).isSameAs(page);
    }
}
