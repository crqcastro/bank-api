package br.com.cesarcastro.bankapi.adapter.in.web.transaction;

import br.com.cesarcastro.bankapi.adapter.in.web.transaction.request.DepositRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.transaction.request.TransferRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.transaction.request.WithdrawRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.transaction.response.BalanceResponse;
import br.com.cesarcastro.bankapi.adapter.in.web.transaction.response.TransactionResponse;
import br.com.cesarcastro.bankapi.adapter.in.web.transaction.response.TransferReceiptResponse;
import br.com.cesarcastro.bankapi.application.port.in.transaction.DepositUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.GetTransactionUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.TransferUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.WithdrawUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Deposits, withdrawals and transfers")
public class TransactionController {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;
    private final GetTransactionUseCase getTransactionUseCase;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Deposit funds into an account")
    public BalanceResponse deposit(@Valid @RequestBody DepositRequest request) {
        DepositUseCase.BalanceResult result = depositUseCase.execute(
                new DepositUseCase.DepositCommand(request.accountId(), request.amount(), request.description()));
        return new BalanceResponse(result.accountId(), result.newBalance(), result.transactionId(), result.timestamp());
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Withdraw funds from an account")
    public BalanceResponse withdraw(@Valid @RequestBody WithdrawRequest request) {
        WithdrawUseCase.BalanceResult result = withdrawUseCase.execute(
                new WithdrawUseCase.WithdrawCommand(request.accountId(), request.amount(), request.description()));
        return new BalanceResponse(result.accountId(), result.newBalance(), result.transactionId(), result.timestamp());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transfer funds between accounts")
    public TransferReceiptResponse transfer(@Valid @RequestBody TransferRequest request) {
        TransferUseCase.TransferResult result = transferUseCase.execute(
                new TransferUseCase.TransferCommand(
                        request.originAccountId(), request.destinationAccountId(),
                        request.amount(), request.description()));
        return new TransferReceiptResponse(result.originAccountId(), result.destinationAccountId(),
                result.amount(), result.originTransactionId(), result.destinationTransactionId(), result.timestamp());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public TransactionResponse getById(@PathVariable UUID id) {
        return TransactionResponse.from(getTransactionUseCase.execute(id));
    }

    @GetMapping
    @Operation(summary = "List transactions by account (statement)")
    public Page<TransactionResponse> list(
            @RequestParam UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            Pageable pageable) {
        return getTransactionUseCase.executeByAccount(accountId, from, to, pageable)
                .map(TransactionResponse::from);
    }
}
