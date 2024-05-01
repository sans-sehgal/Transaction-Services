# Real-time Transaction Services

## Overview

I have built a core banking engine: real-time balance calculation through [event-sourcing](https://martinfowler.com/eaaDev/EventSourcing.html).

## Schema

The [included service.yml](service.yml) is the OpenAPI 3.0 schema to the service I have built. 

## Details

The service accepts two types of transactions:

1. Loads: Add money to a user (credit)

2. Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT returns the updated balance following the transaction. Authorization declines are saved, even if they do not impact balance calculation.

## Directory Structure

```
├── src/
│   ├── main/
│   │   └── java/
│   │       └── dev/
│   │           └── codescreen/
│   │               ├── classes/                 # Contains domain model classes
│   │               │   ├── Amount.java         # Represents the amount class
│   │               │   └── AuthorizationRequest.java  # Represents a transaction authorization request
│   │               ├── events/                 # Contains event classes
│   │               │   ├── AuthorizationEvent.java  # Represents an authorization event inherits from of TransactionEvent
│   │               │   ├── LoadEvent.java      # Represents a load event inherits from TransactionEvent
│   │               │   └── TransactionEvent.java  # Represents main transaction event
│   │               ├── TransactionServiceApplication.java  # Main application class
│   │               └── utils/                  # Contains utility classes
│   │                   ├── EventLoggerPrinter.java  # Utility for logging transaction events
│   │                   └── RequestValidator.java  # Utility for validating transaction requests data
│   └── test/
│       └── java/
│           └── dev/
│               └── codescreen/
│                   ├── RequestValidatorTests.java  # Unit tests for RequestValidator utility
│                   └── TransactionServiceApplicationTests.java  # Integration tests for the application
└── README.md                                     # Project README file


```

## Bootstrap instructions

To run this server locally, follow these steps:

1. Clone the repository from GitHub.
2. Navigate to the project directory.
3. Make sure you have OpenJDK version 11.0.21.
4. Build the project using Maven: `mvn clean install`.
5. Run the application using Maven: `mvn spring-boot:run`.
6. Alternatively, the appliocation can also be run using the `.jar` file: `java -jar ./target/CodeScreen_ikfrumbg-1.0.0.jar`

## Design considerations

### Event Sourcing

The application follows an event sourcing architecture, where each transaction or operation performed in the system is captured as an event. Events such as `AuthorizationEvent` and `LoadEvent` represent specific actions taken on user accounts, including debits and credits. A key design choice was to track the amount in a user account within each event after it's completed. This simplifies reversals, ensuring easy management of account balances.

Domain objects remain untouched, with events created for each request and utilized instead. This approach facilitates seamless replayability, snapshotting application states, straightforward state clearing and rebuilding, and effortless reversals.

By leveraging event sourcing, the application maintains a comprehensive audit trail of all transactions, enabling features such as transaction replay, audit reporting, and system recovery in case of failures. Event sourcing ensures data integrity and consistency by capturing the intent behind each transaction rather than just the resulting state changes.

### Load Endpoint `/load/{messageId}`:

- **Summary**: This endpoint is responsible for adding funds to a user's account.
- **Behavior**:
  1. The endpoint validates the incoming request to ensure that it contains valid information.
  2. If the request is valid, the endpoint updates the user's balance by adding the specified amount of funds.
  3. If the user does not exist, it adds a new user.
  4. It then creates a new load event to log the transaction details.
  5. Finally, it returns a response containing the details of the transaction, including the updated balance.
- **Usage**:
  ```
    curl -X PUT \
      -H "Content-Type: application/json" \
      -d '{
            "userId": "user123",
            "transactionAmount": {
                "amount": "699",
                "currency": "USD",
                "debitOrCredit": "CREDIT"
            }
          }' \
      http://localhost:8080/load/123456789
  ```

### Authorize Endpoint `/authorization/{messageId}`:

- **Summary**: This endpoint is responsible for authorizing debit transactions from a user's account.
- **Behavior**:
  1. The endpoint validates the incoming request to ensure that it contains valid information.
  2. If the request is valid, the endpoint checks if the user's balance is sufficient to cover the debit amount.
  3. If the user does NOT exist, it throws a "User not Found" exception.
  4. If the balance is sufficient, the endpoint authorizes the transaction by updating the user's balance accordingly and creating an authorization event to log the transaction details.
  5. If the balance is insufficient, the transaction is declined.
  6. Finally, the endpoint returns a response containing the details of the transaction, including the authorization status and the updated balance.
- **Usage**:
  ```
    curl -X PUT \
      -H "Content-Type: application/json" \
      -d '{
        "userId": "user123",
        "transactionAmount": {
            "amount": "298",
            "currency": "USD",
            "debitOrCredit": "DEBIT"
        }
      }' \
  http://localhost:8080/authorization/123456789
  ```

### Testing

We employ various types of tests to ensure the correctness and robustness of the Transaction Service application:

- **Unit Tests**: These tests focus on testing individual components of the application, such as utility classes, validators, and domain models. For example, we have `RequestValidatorTests` to validate the behavior of the `RequestValidator` class.

- **Integration Tests**: Integration tests verify the interaction between different components of the application, including controllers, service layers, and data access layers. Our `TransactionServiceApplicationTests` class performs integration testing by using Spring's `MockMvc` to simulate HTTP requests and verify responses.

- **Endpoint Testing**: These tests specifically target the API endpoints to ensure they return the expected responses for various scenarios. For instance, in the `TransactionServiceApplicationTests`, we test the `/load` and `/authorization` endpoints to validate their behavior under different conditions.

We primarily utilize the following libraries for testing:

- **JUnit**: JUnit is a widely-used testing framework for Java applications. It provides annotations and assertions to facilitate the writing and execution of tests.

- **MockMvc**: MockMvc is a testing tool provided by Spring MVC Test framework. It allows us to perform HTTP requests against our application's controllers in a controlled environment, without needing to start a full HTTP server.

- **JsonPath**: JsonPath is a library for querying JSON documents. It enables us to extract specific values from JSON responses returned by our API endpoints, making it easier to validate the correctness of responses in our tests.

## Bonus: Deployment considerations

If I were to deploy this application, I would consider using Docker for containerization and Kubernetes for orchestration. This would allow for easy scalability and management of the application in a cloud environment. Additionally, I would deploy the application to a cloud provider such as AWS or Google Cloud Platform for reliability and scalability.

For the database, I would choose to use a relational database like PostgreSQL or MySQL. These databases offer ACID compliance, strong data integrity, and scalability, which are essential for storing transactional data securely and reliably. Additionally, they are well-supported and have mature ecosystems with tools for backup, monitoring, and management, making them suitable choices for a production environment.
