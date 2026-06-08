package br.com.cesarcastro.bankapi.adapter.in.web.account;

import br.com.cesarcastro.bankapi.adapter.in.web.account.request.BlockAccountRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.account.request.CreateAccountRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.account.response.AccountResponse;
import br.com.cesarcastro.bankapi.application.port.in.account.BlockUnblockAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.CreateAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.DeleteAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.GetAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.InactivateAccountUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final InactivateAccountUseCase inactivateAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final BlockUnblockAccountUseCase blockUnblockAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new account for a customer")
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(createAccountUseCase.execute(request.customerId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public AccountResponse getById(@PathVariable UUID id) {
        return AccountResponse.from(getAccountUseCase.execute(id));
    }

    @GetMapping
    @Operation(summary = "List accounts by customer")
    public Page<AccountResponse> list(@RequestParam UUID customerId, Pageable pageable) {
        return getAccountUseCase.executeByCustomer(customerId, pageable).map(AccountResponse::from);
    }

    @PatchMapping("/{id}/inactivate")
    @Operation(summary = "Inactivate an account")
    public AccountResponse inactivate(@PathVariable UUID id) {
        return AccountResponse.from(inactivateAccountUseCase.execute(id));
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Block an account")
    public AccountResponse block(@PathVariable UUID id, @Valid @RequestBody BlockAccountRequest request) {
        return AccountResponse.from(blockUnblockAccountUseCase.block(id, request.justification()));
    }

    @PatchMapping("/{id}/unblock")
    @Operation(summary = "Unblock an account")
    public AccountResponse unblock(@PathVariable UUID id, @Valid @RequestBody BlockAccountRequest request) {
        return AccountResponse.from(blockUnblockAccountUseCase.unblock(id, request.justification()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an account with zero balance")
    public void delete(@PathVariable UUID id) {
        deleteAccountUseCase.execute(id);
    }
}
