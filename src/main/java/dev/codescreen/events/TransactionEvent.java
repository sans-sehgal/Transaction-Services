package dev.codescreen.events;

public class TransactionEvent {

    public String messageId;
    public String userId;
    public double amount;
    public String currency;
    public String debitOrCredit;
    public double currentUpdatedBalance;

    public TransactionEvent(String messageId, String userId, double amount, String currency, String debitOrCredit,
            double currentUpdatedBalance) {
        this.messageId = messageId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.debitOrCredit = debitOrCredit;
        this.currentUpdatedBalance = currentUpdatedBalance;

    }

}