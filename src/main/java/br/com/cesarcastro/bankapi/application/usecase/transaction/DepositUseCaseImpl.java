package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.DepositUseCase;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.notification.SendNotificationPort;
import br.com.cesarcastro.bankapi.application.port.out.transaction.SaveTransactionPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;
import br.com.cesarcastro.bankapi.infrastructure.exception.BusinessRuleException;
import br.com.cesarcastro.bankapi.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DepositUseCaseImpl implements DepositUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    @Transactional
    @Observed(name = "usecase.transaction.deposit")
    public BalanceResult execute(DepositCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Deposit amount must be greater than zero");
        }
        Account account = loadAccountPort.loadById(command.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", command.accountId()));

        if (account.isBlocked()) {
            throw new BusinessRuleException("Account is blocked");
        }
        if (!account.isActive()) {
            throw new BusinessRuleException("Account is not active");
        }
        account.setBalance(account.getBalance().add(command.amount()));
        Account saved = saveAccountPort.save(account);

        Transaction tx = Transaction.builder()
                .account(saved)
                .type(TransactionType.DEPOSIT)
                .amount(command.amount())
                .description(command.description())
                .build();
        Transaction savedTx = saveTransactionPort.save(tx);

        sendNotificationPort.send(new NotificationEvent(
                saved.getCustomer().getEmail(),
                TransactionType.DEPOSIT,
                command.amount(),
                saved.getBalance(),
                saved.getId(),
                savedTx.getCreatedAt()
        ));

        return new BalanceResult(saved.getId(), saved.getBalance(), savedTx.getId(), savedTx.getCreatedAt());
    }
}
