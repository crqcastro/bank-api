package br.com.cesarcastro.bankapi.application.port.out.email;

public interface SendEmailPort {
    void send(String to, String subject, String htmlBody);
}
