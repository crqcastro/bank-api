package br.com.cesarcastro.bankapi.infrastructure.constants;

public final class BankApiConstants {

    public static final String EXCHANGE_BANK_NOTIFICATIONS = "bank.notifications";
    public static final String EXCHANGE_DLX                = "bank.notifications.dlx";
    public static final String QUEUE_TRANSACTIONS          = "bank.notifications.transactions";
    public static final String QUEUE_DLQ                   = "bank.notifications.transactions.dlq";
    public static final String ROUTING_KEY_TRANSACTION     = "transaction.event";
    public static final String ROUTING_KEY_DLQ             = "transaction.dlq";
    public static final String NOTIFICATION_FROM_EMAIL       = "notification@bank-api.com.br";

    private BankApiConstants() {}
}
