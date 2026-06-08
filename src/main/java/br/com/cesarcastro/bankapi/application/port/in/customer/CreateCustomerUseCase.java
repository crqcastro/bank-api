package br.com.cesarcastro.bankapi.application.port.in.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.DocumentType;

public interface CreateCustomerUseCase {
    Customer execute(CreateCustomerCommand command);

    record CreateCustomerCommand(String name, String document, DocumentType documentType, String email, String phone) {}
}
