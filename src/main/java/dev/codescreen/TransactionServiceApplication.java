package dev.codescreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import dev.codescreen.events.*;
import dev.codescreen.utils.*;
import dev.codescreen.classes.*;

@SpringBootApplication
@RestController
public class TransactionServiceApplication {

    private Map<String, List<TransactionEvent>> eventLog = new HashMap<>();

    public TransactionServiceApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("serverTime", LocalDateTime.now().toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<Map<String, Object>> authorizeTransaction(
            @PathVariable String messageId,
            @RequestBody AuthorizationRequest request) {

        if (!RequestValidator.isValidRequest(request, "AUTHORIZATION")) {
            throw new IllegalArgumentException("Invalid authorization/debit transaction request");
        }

        String userId = request.getUserId();
        double transactionAmount = Double.parseDouble(request.getTransactionAmount().getAmount());
        String currency = request.getTransactionAmount().getCurrency();
        String responseCode = "DECLINED";
        double currentUpdatedBalance;

        if (!eventLog.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<TransactionEvent> userEvents = eventLog.get(userId);
        TransactionEvent lastEvent = userEvents.get(userEvents.size() - 1);
        currentUpdatedBalance = lastEvent.currentUpdatedBalance;

        if (currentUpdatedBalance >= transactionAmount) {
            responseCode = "APPROVED";
            currentUpdatedBalance -= transactionAmount;
        }

        AuthorizationEvent authorizationEvent = new AuthorizationEvent(messageId, userId, transactionAmount, currency,
                responseCode, currentUpdatedBalance);

        eventLog.get(userId).add(authorizationEvent);
        // EventLoggerPrinter.printEventHandler(userId, eventLog.get(userId));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("messageId", messageId);
        responseBody.put("userId", userId);
        responseBody.put("responseCode", responseCode);
        responseBody.put("balance", new Amount(String.valueOf(currentUpdatedBalance), currency, "DEBIT"));

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @PutMapping("/load/{messageId}")
    public ResponseEntity<Map<String, Object>> loadTransaction(@PathVariable String messageId,
            @RequestBody AuthorizationRequest request) {

        if (!RequestValidator.isValidRequest(request, "LOAD")) {
            throw new IllegalArgumentException("Invalid load transaction request");
        }

        String userId = request.getUserId();
        double transactionAmount = Double.parseDouble(request.getTransactionAmount().getAmount());
        String currency = request.getTransactionAmount().getCurrency();
        double currentUpdatedBalance;

        if (!eventLog.containsKey(userId)) {
            eventLog.put(userId, new ArrayList<>());
            currentUpdatedBalance = transactionAmount;
        } else {
            List<TransactionEvent> userEvents = eventLog.get(userId);
            TransactionEvent lastEvent = userEvents.get(userEvents.size() - 1);
            currentUpdatedBalance = lastEvent.currentUpdatedBalance + transactionAmount;
        }

        LoadEvent loadEvent = new LoadEvent(messageId, userId, transactionAmount, currency, currentUpdatedBalance);

        eventLog.get(userId).add(loadEvent);
        // EventLoggerPrinter.printEventHandler(userId, eventLog.get(userId));

        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("messageId", messageId);
        responseBody.put("userId", userId);
        responseBody.put("balance", new Amount(String.valueOf(currentUpdatedBalance),
                currency, "CREDIT"));

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        System.out.println("hit this position!");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
