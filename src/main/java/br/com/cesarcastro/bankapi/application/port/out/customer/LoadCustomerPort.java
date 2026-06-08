package br.com.cesarcastro.bankapi.application.port.out.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface LoadCustomerPort {
    Optional<Customer> loadById(UUID id);
    Optional<Customer> loadByDocument(String document);
    Page<Customer> loadAll(CustomerStatus status, Pageable pageable);
    boolean existsByDocument(String document);
    boolean hasActiveAccounts(UUID customerId);
}
