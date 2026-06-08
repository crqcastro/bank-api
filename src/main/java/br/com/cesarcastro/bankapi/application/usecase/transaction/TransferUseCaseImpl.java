package br.com.cesarcastro.bankapi.application.usecase.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.TransferUseCase;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    @Transactional
    @Observed(name = "usecase.transaction.transfer")
    public TransferResult execute(TransferCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Transfer amount must be greater than zero");
        }
        if (command.originAccountId().equals(command.destinationAccountId())) {
            throw new BusinessRuleException("Origin and destination accounts must be different");
        }

        // Acquire locks in deterministic UUID order to prevent deadlock
        UUID firstId  = command.originAccountId().compareTo(command.destinationAccountId()) < 0
                ? command.originAccountId() : command.destinationAccountId();
        UUID secondId = firstId.equals(command.originAccountId())
                ? command.destinationAccountId() : command.originAccountId();

        Account first  = loadAccountPort.loadByIdForUpdate(firstId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", firstId));
        Account second = loadAccountPort.loadByIdForUpdate(secondId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", secondId));

        Account origin      = first.getId().equals(command.originAccountId()) ? first : second;
        Account destination = first.getId().equals(command.originAccountId()) ? second : first;

        validateAccount(origin, "origin");
        validateAccount(destination, "destination");

        if (!origin.hasSufficientBalance(command.amount())) {
            throw new BusinessRuleException("Insufficient balance in origin account");
        }

        origin.setBalance(origin.getBalance().subtract(command.amount()));
        destination.setBalance(destination.getBalance().add(command.amount()));

        saveAccountPort.save(origin);
        saveAccountPort.save(destination);

        Transaction originTx = saveTransactionPort.save(Transaction.builder()
                .account(origin)
                .type(TransactionType.TRANSFER_OUT)
                .amount(command.amount())
                .description(command.description())
                .build());

        Transaction destTx = saveTransactionPort.save(Transaction.builder()
                .account(destination)
                .type(TransactionType.TRANSFER_IN)
                .amount(command.amount())
                .description(command.description())
                .build());

        sendNotificationPort.send(new NotificationEvent(
                origin.getCustomer().getEmail(), TransactionType.TRANSFER_OUT,
                command.amount(), origin.getBalance(), origin.getId(), originTx.getCreatedAt()));

        sendNotificationPort.send(new NotificationEvent(
                destination.getCustomer().getEmail(), TransactionType.TRANSFER_IN,
                command.amount(), destination.getBalance(), destination.getId(), destTx.getCreatedAt()));

        return new TransferResult(
                origin.getId(), destination.getId(), command.amount(),
                originTx.getId(), destTx.getId(), originTx.getCreatedAt());
    }

    private void validateAccount(Account account, String role) {
        if (account.isBlocked()) {
            throw new BusinessRuleException("The " + role + " account is blocked");
        }
        if (!account.isActive()) {
            throw new BusinessRuleException("The " + role + " account is not active");
        }
    }
}
