package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.in.account.GetAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAccountUseCaseImpl implements GetAccountUseCase {

    private final LoadAccountPort loadAccountPort;

    @Override
    @Transactional(readOnly = true)
    public Account execute(UUID id) {
        return loadAccountPort.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> executeByCustomer(UUID customerId, Pageable pageable) {
        return loadAccountPort.loadByCustomerId(customerId, pageable);
    }
}
