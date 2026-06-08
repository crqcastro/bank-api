package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.DeleteCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.out.customer.DeleteCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final DeleteCustomerPort deleteCustomerPort;

    @Override
    @Transactional
    @Observed(name = "usecase.customer.delete")
    public void execute(UUID customerId) {
        Customer customer = loadCustomerPort.loadById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        if (deleteCustomerPort.hasAccounts(customer.getId())) {
            throw new BusinessRuleException("Customer has accounts and cannot be deleted");
        }
        deleteCustomerPort.deleteById(customerId);
    }
}
