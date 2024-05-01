package dev.codescreen.events;

public class AuthorizationEvent extends TransactionEvent {

    public String responseCode;

    public AuthorizationEvent(String messageId, String userId, double amount, String currency,
            String responseCode, double currentUpdatedBalance) {
        super(messageId, userId, amount, currency, "DEBIT", currentUpdatedBalance);
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "AuthorizeEvent{" +
                "messageId='" + messageId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", debitOrCredit='" + debitOrCredit + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ",currentUpdatedBalance='" + currentUpdatedBalance + '\'' +
                '}';
    }

}