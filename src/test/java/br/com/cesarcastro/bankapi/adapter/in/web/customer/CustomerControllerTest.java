package br.com.cesarcastro.bankapi.adapter.in.web.customer;

import br.com.cesarcastro.bankapi.application.port.in.customer.CreateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.DeactivateCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.DeleteCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.GetCustomerUseCase;
import br.com.cesarcastro.bankapi.application.port.in.customer.UpdateCustomerUseCase;
import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import br.com.cesarcastro.bankapi.domain.model.DocumentType;
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

import java.util.HashMap;
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
@DisplayName("CustomerController")
class CustomerControllerTest {

    @Mock CreateCustomerUseCase createCustomerUseCase;
    @Mock UpdateCustomerUseCase updateCustomerUseCase;
    @Mock DeactivateCustomerUseCase deactivateCustomerUseCase;
    @Mock DeleteCustomerUseCase deleteCustomerUseCase;
    @Mock GetCustomerUseCase getCustomerUseCase;
    @InjectMocks CustomerController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private Customer customer() {
        return Customer.builder().id(UUID.randomUUID()).name("John").document("12345678901")
                .documentType(DocumentType.CPF).email("john@bank.com").phone("11999999999")
                .status(CustomerStatus.ACTIVE).build();
    }

    @Test
    @DisplayName("POST /customers deve retornar 201")
    void createReturns201() throws Exception {
        when(createCustomerUseCase.execute(any())).thenReturn(customer());

        Map<String, Object> body = new HashMap<>();
        body.put("name", "John");
        body.put("document", "12345678901");
        body.put("documentType", "CPF");
        body.put("email", "john@bank.com");
        body.put("phone", "11999999999");

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /customers/{id} deve retornar 200")
    void getByIdReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(getCustomerUseCase.execute(id)).thenReturn(customer());

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /customers deve retornar 200")
    void listReturns200() throws Exception {
        when(getCustomerUseCase.executeAll(any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /customers/{id} deve retornar 200")
    void updateReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(updateCustomerUseCase.execute(any())).thenReturn(customer());

        mockMvc.perform(patch("/api/v1/customers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "new@bank.com"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /customers/{id}/deactivate deve retornar 204")
    void deactivateReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deactivateCustomerUseCase).execute(id);

        mockMvc.perform(delete("/api/v1/customers/{id}/deactivate", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /customers/{id} deve retornar 204")
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteCustomerUseCase).execute(id);

        mockMvc.perform(delete("/api/v1/customers/{id}", id))
                .andExpect(status().isNoContent());
    }
}
