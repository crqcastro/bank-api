package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.in.account.DeleteAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.out.account.DeleteAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
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
public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {

    private final LoadAccountPort loadAccountPort;
    private final DeleteAccountPort deleteAccountPort;

    @Override
    @Transactional
    @Observed(name = "usecase.account.delete")
    public void execute(UUID accountId) {
        Account account = loadAccountPort.loadById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("Account balance must be zero to delete");
        }
        deleteAccountPort.deleteById(accountId);
    }
}
