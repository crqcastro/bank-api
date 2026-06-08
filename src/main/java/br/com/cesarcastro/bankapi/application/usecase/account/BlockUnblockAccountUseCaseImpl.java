package br.com.cesarcastro.bankapi.application.usecase.account;

import br.com.cesarcastro.bankapi.application.port.in.account.BlockUnblockAccountUseCase;
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
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockUnblockAccountUseCaseImpl implements BlockUnblockAccountUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    @Transactional
    @Observed(name = "usecase.account.block")
    public Account block(UUID accountId, String justification) {
        if (!StringUtils.hasText(justification)) {
            throw new BusinessRuleException("Justification is required to block an account");
        }
        Account account = loadAccountPort.loadById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (account.isBlocked()) {
            throw new BusinessRuleException("Account is already blocked");
        }
        account.setStatus(AccountStatus.BLOCKED);
        return saveAccountPort.save(account);
    }

    @Override
    @Transactional
    @Observed(name = "usecase.account.unblock")
    public Account unblock(UUID accountId, String justification) {
        if (!StringUtils.hasText(justification)) {
            throw new BusinessRuleException("Justification is required to unblock an account");
        }
        Account account = loadAccountPort.loadById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.isBlocked()) {
            throw new BusinessRuleException("Account is not blocked");
        }
        account.setStatus(AccountStatus.ACTIVE);
        return saveAccountPort.save(account);
    }
}
