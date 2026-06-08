package br.com.cesarcastro.bankapi.application.port.out.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;

public interface SaveCustomerPort {
    Customer save(Customer customer);
}
