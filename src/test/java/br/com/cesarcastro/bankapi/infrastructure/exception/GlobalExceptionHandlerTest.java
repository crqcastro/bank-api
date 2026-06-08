package br.com.cesarcastro.bankapi.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest req;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("handleNotFound deve retornar 404")
    void handleNotFoundReturns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Account", UUID.randomUUID());

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
    }


    @Test
    @DisplayName("ResourceNotFoundException com mensagem simples deve construir corretamente")
    void resourceNotFoundWithMessageConstructor() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Custom not found message");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("Custom not found message");
    }
    @Test
    @DisplayName("handleBusinessRule deve retornar 422")
    void handleBusinessRuleReturns422() {
        BusinessRuleException ex = new BusinessRuleException("Insufficient balance");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().error()).isEqualTo("Business Rule Violation");
    }

    @Test
    @DisplayName("handleDuplicate deve retornar 409")
    void handleDuplicateReturns409() {
        DuplicateResourceException ex = new DuplicateResourceException("document already exists");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
    }

    @Test
    @DisplayName("handleValidation deve retornar 400 com mensagens dos campos")
    void handleValidationReturns400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("obj", "amount", "must be positive")));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("must be positive");
    }

    @Test
    @DisplayName("handleConstraint deve retornar 400")
    void handleConstraintReturns400() {
        ConstraintViolationException ex = new ConstraintViolationException("invalid value", Set.of());

        ResponseEntity<ErrorResponse> response = handler.handleConstraint(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo("Validation Error");
    }

    @Test
    @DisplayName("handleGeneric deve retornar 500")
    void handleGenericReturns500() {
        Exception ex = new RuntimeException("unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }
}
