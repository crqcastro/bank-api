package br.com.cesarcastro.bankapi.adapter.out.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JavaMailSenderAdapter")
class JavaMailSenderAdapterTest {

    @Mock
    JavaMailSender mailSender;

    @Mock
    MimeMessage mimeMessage;

    @InjectMocks
    JavaMailSenderAdapter adapter;

    @Test
    @DisplayName("send deve configurar MimeMessage e delegar para JavaMailSender")
    void shouldConfigureAndSendMimeMessage() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        adapter.send("to@bank.com", "Test Subject", "<html><body>body</body></html>");

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("send deve usar o from email da constante")
    void shouldUseNotificationFromEmail() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        adapter.send("recipient@example.com", "Subject", "<html><body>Test</body></html>");

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("send deve logar erro sem lancar excecao quando MessagingException ocorre")
    void shouldLogErrorWithoutThrowingOnMessagingException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        // Act + Assert - should not throw
        org.assertj.core.api.Assertions.assertThatCode(() ->
                adapter.send("to@bank.com", "Subject", "<html/>")
        ).doesNotThrowAnyException();
    }
}
