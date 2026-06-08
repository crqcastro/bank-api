package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.in.account.CreateAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
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
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    @Transactional
    @Observed(name = "usecase.account.create")
    public Account execute(UUID customerId) {
        Customer customer = loadCustomerPort.loadById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        if (!customer.isActive()) {
            throw new BusinessRuleException("Cannot create account for inactive customer");
        }
        Account account = Account.builder()
                .customer(customer)
                .build();
        return saveAccountPort.save(account);
    }
}
