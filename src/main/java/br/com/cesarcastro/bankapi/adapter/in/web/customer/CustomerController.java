package br.com.cesarcastro.bankapi.adapter.in.web.customer;

import br.com.cesarcastro.bankapi.adapter.in.web.customer.request.CreateCustomerRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.customer.request.UpdateCustomerRequest;
import br.com.cesarcastro.bankapi.adapter.in.web.customer.response.CustomerResponse;
import br.com.cesarcastro.bankapi.application.port.in.customer.CreateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.DeactivateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.DeleteCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.GetCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.UpdateCustomerUseCase;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer")
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        return CustomerResponse.from(createCustomerUseCase.execute(
                new CreateCustomerUseCase.CreateCustomerCommand(
                        request.name(), request.document(), request.documentType(),
                        request.email(), request.phone())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public CustomerResponse getById(@PathVariable UUID id) {
        return CustomerResponse.from(getCustomerUseCase.execute(id));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    public Page<CustomerResponse> list(@RequestParam(required = false) CustomerStatus status, Pageable pageable) {
        return getCustomerUseCase.executeAll(status, pageable).map(CustomerResponse::from);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update customer data")
    public CustomerResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
        return CustomerResponse.from(updateCustomerUseCase.execute(
                new UpdateCustomerUseCase.UpdateCustomerCommand(
                        id, request.name(), request.nameChangeJustification(),
                        request.email(), request.phone())));
    }

    @DeleteMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logically deactivate a customer")
    public void deactivate(@PathVariable UUID id) {
        deactivateCustomerUseCase.execute(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Permanently delete a customer")
    public void delete(@PathVariable UUID id) {
        deleteCustomerUseCase.execute(id);
    }
}
