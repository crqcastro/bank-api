package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.in.account.InactivateAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InactivateAccountUseCaseImpl implements InactivateAccountUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    @Transactional
    @Observed(name = "usecase.account.inactivate")
    public Account execute(UUID accountId) {
        Account account = loadAccountPort.loadById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.isActive()) {
            throw new BusinessRuleException("Account is not active");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("Account balance must be zero to inactivate");
        }
        account.setStatus(AccountStatus.INACTIVE);
        return saveAccountPort.save(account);
    }
}
