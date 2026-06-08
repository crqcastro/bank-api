package br.com.cesarcastro.bankapi.application.port.in.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetCustomerUseCase {
    Customer execute(UUID id);
    Page<Customer> executeAll(CustomerStatus status, Pageable pageable);
}
