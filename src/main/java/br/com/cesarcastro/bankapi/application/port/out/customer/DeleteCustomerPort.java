package br.com.cesarcastro.bankapi.application.port.out.customer;

import java.util.UUID;

public interface DeleteCustomerPort {
    void deleteById(UUID id);
    boolean hasAccounts(UUID customerId);
}
