package br.com.cesarcastro.bankapi.adapter.in.web.transaction;

import br.com.cesarcastro.bankapi.application.port.in.transaction.DepositUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.GetTransactionUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.TransferUseCase;
import br.com.cesarcastro.bankapi.application.port.in.transaction.WithdrawUseCase;
import br.com.cesarcastro.bankapi.domain.model.Account;
import br.com.cesarcastro.bankapi.domain.model.Transaction;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionController")
class TransactionControllerTest {

    @Mock DepositUseCase depositUseCase;
    @Mock WithdrawUseCase withdrawUseCase;
    @Mock TransferUseCase transferUseCase;
    @Mock GetTransactionUseCase getTransactionUseCase;
    @InjectMocks TransactionController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private DepositUseCase.BalanceResult balanceResult() {
        return new DepositUseCase.BalanceResult(
                UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID(), OffsetDateTime.now());
    }

    private Transaction transaction() {
        Account account = Account.builder().id(UUID.randomUUID()).build();
        return Transaction.builder().id(UUID.randomUUID()).account(account)
                .type(TransactionType.DEPOSIT).amount(BigDecimal.TEN).build();
    }

    @Test
    @DisplayName("POST /transactions/deposit deve retornar 201")
    void depositReturns201() throws Exception {
        when(depositUseCase.execute(any())).thenReturn(balanceResult());

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", UUID.randomUUID().toString());
        body.put("amount", 100);
        body.put("description", null);

        mockMvc.perform(post("/api/v1/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /transactions/withdraw deve retornar 201")
    void withdrawReturns201() throws Exception {
        WithdrawUseCase.BalanceResult result = new WithdrawUseCase.BalanceResult(
                UUID.randomUUID(), BigDecimal.TEN, UUID.randomUUID(), OffsetDateTime.now());
        when(withdrawUseCase.execute(any())).thenReturn(result);

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", UUID.randomUUID().toString());
        body.put("amount", 50);
        body.put("description", null);

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /transactions/transfer deve retornar 201")
    void transferReturns201() throws Exception {
        TransferUseCase.TransferResult result = new TransferUseCase.TransferResult(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN,
                UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now());
        when(transferUseCase.execute(any())).thenReturn(result);

        Map<String, Object> body = new HashMap<>();
        body.put("originAccountId", UUID.randomUUID().toString());
        body.put("destinationAccountId", UUID.randomUUID().toString());
        body.put("amount", 100);
        body.put("description", null);

        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /transactions/{id} deve retornar 200")
    void getByIdReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(getTransactionUseCase.execute(id)).thenReturn(transaction());

        mockMvc.perform(get("/api/v1/transactions/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /transactions deve retornar 200")
    void listReturns200() throws Exception {
        when(getTransactionUseCase.executeByAccount(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/api/v1/transactions").param("accountId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }
}
