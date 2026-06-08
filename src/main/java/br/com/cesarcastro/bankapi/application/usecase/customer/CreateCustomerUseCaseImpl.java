package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.CreateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.infrastructure.exception.DuplicateResourceException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveCustomerPort saveCustomerPort;

    @Override
    @Transactional
    @Observed(name = "usecase.customer.create")
    public Customer execute(CreateCustomerCommand command) {
        if (loadCustomerPort.existsByDocument(command.document())) {
            throw new DuplicateResourceException("Customer already exists with document: " + command.document());
        }
        Customer customer = Customer.builder()
                .name(command.name())
                .document(command.document())
                .documentType(command.documentType())
                .email(command.email())
                .phone(command.phone())
                .build();
        return saveCustomerPort.save(customer);
    }
}
