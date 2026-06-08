package br.com.cesarcastro.bankapi.application.port.in.customer;

import java.util.UUID;

public interface DeactivateCustomerUseCase {
    void execute(UUID customerId);
}
