package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.DeactivateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateCustomerUseCaseImpl implements DeactivateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveCustomerPort saveCustomerPort;

    @Override
    @Transactional
    @Observed(name = "usecase.customer.deactivate")
    public void execute(UUID customerId) {
        Customer customer = loadCustomerPort.loadById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        if (!customer.isActive()) {
            throw new BusinessRuleException("Customer is already inactive");
        }
        if (loadCustomerPort.hasActiveAccounts(customerId)) {
            throw new BusinessRuleException("Customer has active accounts and cannot be deactivated");
        }
        customer.setStatus(CustomerStatus.INACTIVE);
        saveCustomerPort.save(customer);
    }
}
