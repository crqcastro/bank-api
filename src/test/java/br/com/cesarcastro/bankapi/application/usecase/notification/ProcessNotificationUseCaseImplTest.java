package br.com.cesarcastro.bankapi.application.usecase.notification;

import br.com.cesarcastro.bankapi.application.port.out.email.SendEmailPort;
import br.com.cesarcastro.bankapi.domain.model.NotificationEvent;
import br.com.cesarcastro.bankapi.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessNotificationUseCaseImpl")
class ProcessNotificationUseCaseImplTest {

    @Mock
    SendEmailPort sendEmailPort;

    @InjectMocks
    ProcessNotificationUseCaseImpl useCase;

    private NotificationEvent buildEvent(TransactionType type) {
        return new NotificationEvent(
                "user@bank.com",
                type,
                BigDecimal.valueOf(150.00),
                BigDecimal.valueOf(350.00),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    @Test
    @DisplayName("deve chamar sendEmailPort com os argumentos corretos")
    void shouldCallSendEmailPortWithCorrectArgs() {
        // Arrange
        NotificationEvent event = buildEvent(TransactionType.DEPOSIT);
        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(toCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());
        assertThat(toCaptor.getValue()).isEqualTo("user@bank.com");
        assertThat(subjectCaptor.getValue()).contains("[Bank API]");
        assertThat(bodyCaptor.getValue()).contains("<html>");
    }

    @Test
    @DisplayName("deve gerar subject correto para DEPOSIT")
    void shouldBuildCorrectSubjectForDeposit() {
        // Arrange
        NotificationEvent event = buildEvent(TransactionType.DEPOSIT);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(org.mockito.ArgumentMatchers.any(), subjectCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertThat(subjectCaptor.getValue()).startsWith("[Bank API] Deposito de R$");
    }

    @Test
    @DisplayName("deve gerar subject correto para WITHDRAWAL")
    void shouldBuildCorrectSubjectForWithdrawal() {
        // Arrange
        NotificationEvent event = buildEvent(TransactionType.WITHDRAWAL);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(org.mockito.ArgumentMatchers.any(), subjectCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertThat(subjectCaptor.getValue()).startsWith("[Bank API] Saque de R$");
    }

    @Test
    @DisplayName("deve gerar subject correto para TRANSFER_OUT")
    void shouldBuildCorrectSubjectForTransferOut() {
        // Arrange
        NotificationEvent event = buildEvent(TransactionType.TRANSFER_OUT);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(org.mockito.ArgumentMatchers.any(), subjectCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertThat(subjectCaptor.getValue()).startsWith("[Bank API] Transferencia de R$");
        assertThat(subjectCaptor.getValue()).endsWith("enviada");
    }

    @Test
    @DisplayName("deve gerar subject correto para TRANSFER_IN")
    void shouldBuildCorrectSubjectForTransferIn() {
        // Arrange
        NotificationEvent event = buildEvent(TransactionType.TRANSFER_IN);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(org.mockito.ArgumentMatchers.any(), subjectCaptor.capture(), org.mockito.ArgumentMatchers.any());
        assertThat(subjectCaptor.getValue()).startsWith("[Bank API] Transferencia de R$");
        assertThat(subjectCaptor.getValue()).endsWith("recebida");
    }

    @Test
    @DisplayName("deve incluir dados da conta no corpo do email")
    void shouldIncludeAccountDataInEmailBody() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        NotificationEvent event = new NotificationEvent(
                "recipient@bank.com",
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(200.50),
                BigDecimal.valueOf(500.75),
                accountId,
                OffsetDateTime.now()
        );
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        useCase.execute(event);

        // Assert
        verify(sendEmailPort).send(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), bodyCaptor.capture());
        String body = bodyCaptor.getValue();
        assertThat(body).contains(accountId.toString());
        assertThat(body).contains("200,50");
        assertThat(body).contains("500,75");
    }
}
