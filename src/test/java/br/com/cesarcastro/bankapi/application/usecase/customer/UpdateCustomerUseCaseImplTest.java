package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.UpdateCustomerUseCase.UpdateCustomerCommand;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCustomerUseCase")
class UpdateCustomerUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @Mock SaveCustomerPort saveCustomerPort;
    @InjectMocks UpdateCustomerUseCaseImpl useCase;

    @Test
    @DisplayName("deve atualizar nome, email e telefone (RN004)")
    void executeShouldUpdateAllFields() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).name("Old").email("old@x.com").build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));
        when(saveCustomerPort.save(any())).thenReturn(customer);

        UpdateCustomerCommand cmd = new UpdateCustomerCommand(
                id, "New Name", "Legal reason", "new@x.com", "999999999");
        Customer result = useCase.execute(cmd);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@x.com");
        assertThat(result.getPhone()).isEqualTo("999999999");
    }

    @Test
    @DisplayName("deve lancar excecao quando nome mudar sem justificativa (RN004)")
    void executeShouldThrowWhenNameChangeLacksJustification() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).build();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.of(customer));

        UpdateCustomerCommand cmd = new UpdateCustomerCommand(id, "New Name", "", null, null);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("justification");
    }

    @Test
    @DisplayName("deve lancar excecao quando cliente nao existir")
    void executeShouldThrowWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(loadCustomerPort.loadById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new UpdateCustomerCommand(id, null, null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
