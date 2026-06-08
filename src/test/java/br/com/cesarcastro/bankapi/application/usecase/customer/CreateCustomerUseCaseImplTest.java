package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.CreateCustomerUseCase.CreateCustomerCommand;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.DocumentType;
import br.com.cesarcastro.bankapi.infrastructure.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCustomerUseCase")
class CreateCustomerUseCaseImplTest {

    @Mock LoadCustomerPort loadCustomerPort;
    @Mock SaveCustomerPort saveCustomerPort;
    @InjectMocks CreateCustomerUseCaseImpl useCase;

    @Test
    @DisplayName("deve criar cliente quando o documento for unico")
    void shouldCreateCustomerWhenDocumentIsUnique() {
        var command = new CreateCustomerCommand("Joao Silva", "12345678901", DocumentType.CPF, "joao@email.com", null);
        var saved = Customer.builder().name("Joao Silva").document("12345678901").build();
        when(loadCustomerPort.existsByDocument("12345678901")).thenReturn(false);
        when(saveCustomerPort.save(any())).thenReturn(saved);

        Customer result = useCase.execute(command);

        assertThat(result.getName()).isEqualTo("Joao Silva");
        verify(saveCustomerPort).save(any());
    }

    @Test
    @DisplayName("deve lancar excecao quando o documento ja existir (RN001)")
    void shouldThrowWhenDocumentAlreadyExists() {
        var command = new CreateCustomerCommand("Joao", "12345678901", DocumentType.CPF, "joao@email.com", null);
        when(loadCustomerPort.existsByDocument("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("12345678901");

        verify(saveCustomerPort, never()).save(any());
    }
}
