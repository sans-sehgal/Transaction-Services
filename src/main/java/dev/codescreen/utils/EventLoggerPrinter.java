package dev.codescreen.utils;

import java.util.List;

import dev.codescreen.events.TransactionEvent;

public class EventLoggerPrinter {
    public static void printEventHandler(String userId, List<TransactionEvent> events) {
        if (events != null) {
            System.out.println("Event Log for User: " + userId);
            System.out.println("-------------------------------------------");

            for (TransactionEvent event : events) {
                System.out.println(event.toString());
            }

            System.out.println("-------------------------------------------");
        } else {
            System.out.println("No event log found for user: " + userId);
        }
    }
}
