package br.com.cesarcastro.bankapi.adapter.in.web.account;

import br.com.cesarcastro.bankapi.application.port.in.account.BlockUnblockAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.CreateAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.DeleteAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.GetAccountUseCase;
import br.com.cesarcastro.bankapi.application.port.in.account.InactivateAccountUseCase;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.AccountStatus;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountController")
class AccountControllerTest {

    @Mock CreateAccountUseCase createAccountUseCase;
    @Mock InactivateAccountUseCase inactivateAccountUseCase;
    @Mock DeleteAccountUseCase deleteAccountUseCase;
    @Mock BlockUnblockAccountUseCase blockUnblockAccountUseCase;
    @Mock GetAccountUseCase getAccountUseCase;
    @InjectMocks AccountController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private Account account() {
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        return Account.builder().id(UUID.randomUUID()).customer(customer)
                .status(AccountStatus.ACTIVE).balance(BigDecimal.ZERO).build();
    }

    @Test
    @DisplayName("POST /accounts deve retornar 201")
    void createReturns201() throws Exception {
        when(createAccountUseCase.execute(any())).thenReturn(account());

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("customerId", UUID.randomUUID()))))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /accounts/{id} deve retornar 200")
    void getByIdReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(getAccountUseCase.execute(id)).thenReturn(account());

        mockMvc.perform(get("/api/v1/accounts/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /accounts deve retornar 200")
    void listReturns200() throws Exception {
        when(getAccountUseCase.executeByCustomer(any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/api/v1/accounts").param("customerId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /accounts/{id}/inactivate deve retornar 200")
    void inactivateReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(inactivateAccountUseCase.execute(id)).thenReturn(account());

        mockMvc.perform(patch("/api/v1/accounts/{id}/inactivate", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /accounts/{id}/block deve retornar 200")
    void blockReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(blockUnblockAccountUseCase.block(any(), any())).thenReturn(account());

        mockMvc.perform(patch("/api/v1/accounts/{id}/block", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("justification", "Fraud"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /accounts/{id}/unblock deve retornar 200")
    void unblockReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(blockUnblockAccountUseCase.unblock(any(), any())).thenReturn(account());

        mockMvc.perform(patch("/api/v1/accounts/{id}/unblock", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("justification", "Cleared"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /accounts/{id} deve retornar 204")
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteAccountUseCase).execute(id);

        mockMvc.perform(delete("/api/v1/accounts/{id}", id))
                .andExpect(status().isNoContent());
    }
}
