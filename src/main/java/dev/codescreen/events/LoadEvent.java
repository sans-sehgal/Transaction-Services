package dev.codescreen.events;

public class LoadEvent extends TransactionEvent {
    public LoadEvent(String messageId, String userId, double amount, String currency,
            double currentUpdatedBalance) {
        super(messageId, userId, amount, currency, "CREDIT", currentUpdatedBalance);
    }

    @Override
    public String toString() {
        return "LoadEvent{" +
                "messageId='" + messageId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", debitOrCredit='" + debitOrCredit + '\'' +
                ",currentUpdatedBalance='" + currentUpdatedBalance + '\'' +
                '}';
    }

}