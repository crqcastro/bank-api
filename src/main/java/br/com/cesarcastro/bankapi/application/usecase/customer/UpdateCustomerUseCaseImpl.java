package br.com.cesarcastro.bankapi.application.usecase.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.UpdateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.out.customer.LoadCustomerPort;
import br.com.cesarcastro.bankapi.application.port.out.customer.SaveCustomerPort;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveCustomerPort saveCustomerPort;

    @Override
    @Transactional
    @Observed(name = "usecase.customer.update")
    public Customer execute(UpdateCustomerCommand command) {
        Customer customer = loadCustomerPort.loadById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", command.id()));

        if (StringUtils.hasText(command.name())) {
            if (!StringUtils.hasText(command.nameChangeJustification())) {
                throw new BusinessRuleException("Name change requires a justification");
            }
            customer.setName(command.name());
        }
        if (StringUtils.hasText(command.email())) {
            customer.setEmail(command.email());
        }
        if (command.phone() != null) {
            customer.setPhone(command.phone());
        }
        return saveCustomerPort.save(customer);
    }
}
