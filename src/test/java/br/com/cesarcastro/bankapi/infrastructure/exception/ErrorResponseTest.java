package br.com.cesarcastro.bankapi.infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorResponse")
class ErrorResponseTest {

    @Test
    @DisplayName("deve preservar todos os campos do record")
    void shouldPreserveAllFields() {
        OffsetDateTime ts = OffsetDateTime.now();

        ErrorResponse response = new ErrorResponse(ts, 404, "Not Found", "Resource not found", "/api/test");

        assertThat(response.timestamp()).isEqualTo(ts);
        assertThat(response.status()).isEqualTo(404);
        assertThat(response.error()).isEqualTo("Not Found");
        assertThat(response.message()).isEqualTo("Resource not found");
        assertThat(response.path()).isEqualTo("/api/test");
    }
}
