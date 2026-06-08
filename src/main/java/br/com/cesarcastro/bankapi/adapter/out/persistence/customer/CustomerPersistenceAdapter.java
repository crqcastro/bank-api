package br.com.cesarcastro.bankapi.adapter.out.persistence.customer;

import br.com.cesarcastro.bankapi.application.port.out.customer.DeleteCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements LoadCustomerPort, SaveCustomerPort, DeleteCustomerPort {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Optional<Customer> loadById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Customer> loadByDocument(String document) {
        return jpaRepository.findByDocument(document);
    }

    @Override
    public Page<Customer> loadAll(CustomerStatus status, Pageable pageable) {
        if (status != null) {
            return jpaRepository.findByStatus(status, pageable);
        }
        return jpaRepository.findAll(pageable);
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public boolean hasActiveAccounts(UUID customerId) {
        return jpaRepository.hasActiveAccounts(customerId);
    }

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(customer);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean hasAccounts(UUID customerId) {
        return jpaRepository.hasAccounts(customerId);
    }
}
