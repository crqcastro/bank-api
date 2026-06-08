package br.com.cesarcastro.bankapi.application.port.in.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;

import java.util.UUID;

public interface UpdateCustomerUseCase {
    Customer execute(UpdateCustomerCommand command);

    record UpdateCustomerCommand(UUID id, String name, String nameChangeJustification, String email, String phone) {}
}
