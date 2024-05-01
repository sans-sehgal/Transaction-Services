package dev.codescreen.utils;

import dev.codescreen.classes.Amount;
import dev.codescreen.classes.AuthorizationRequest;

public class RequestValidator {
    public static boolean isValidRequest(AuthorizationRequest request, String requestType) {
        return request != null && request.getUserId() != null
                && isValidAmount(request.getTransactionAmount(), requestType);
    }

    public static boolean isValidAmount(Amount amount, String requestType) {
        if (amount == null || amount.getAmount() == null || amount.getCurrency() == null ||
                amount.getDebitOrCredit() == null) {
            return false;
        }

        // Check if the amount is a valid numeric value and not negative
        try {
            double parsedAmount = Double.parseDouble(amount.getAmount());
            if (parsedAmount < 0) {
                return false; // Amount should not be negative
            }
        } catch (NumberFormatException e) {
            return false; // Invalid amount format
        }

        // Check if the currency is USD
        if (!"USD".equalsIgnoreCase(amount.getCurrency())) {
            return false; // Only USD currency is allowed
        }

        // Check if debitOrCredit is either "DEBIT" or "CREDIT"
        if ("LOAD".equalsIgnoreCase(requestType) && !"CREDIT".equalsIgnoreCase(amount.getDebitOrCredit())) {
            return false; // For LOAD request, debitOrCredit must be "CREDIT"
        } else if ("AUTHORIZATION".equalsIgnoreCase(requestType)
                && !"DEBIT".equalsIgnoreCase(amount.getDebitOrCredit())) {
            return false; // For Authorization request, debitOrCredit must be "DEBIT"
        }
        return true; // All checks passed
    }
}
