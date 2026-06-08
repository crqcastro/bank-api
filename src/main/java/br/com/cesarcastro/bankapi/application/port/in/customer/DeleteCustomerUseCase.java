package br.com.cesarcastro.bankapi.application.port.in.customer;

import java.util.UUID;

public interface DeleteCustomerUseCase {
    void execute(UUID customerId);
}
