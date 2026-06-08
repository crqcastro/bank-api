package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.GetCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCustomerUseCaseImpl implements GetCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;

    @Override
    @Transactional(readOnly = true)
    public Customer execute(UUID id) {
        return loadCustomerPort.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> executeAll(CustomerStatus status, Pageable pageable) {
        return loadCustomerPort.loadAll(status, pageable);
    }
}
