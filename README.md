# Tiny Ledger

* A simple in-memory ledger application built with Spring Boot (Java 17). It supports:
  - Recording deposits and withdrawals
  - Viewing the current balance
  - Viewing transaction history

* The data is stored **in-memory**, so it resets when the application restarts.

## Assumptions
1. **Multiple Accounts**
    - Each account has a unique `accountNumber`, validated by a regex pattern (e.g. IBAN-like format).
    - An `Account` can have its own balance, stored in memory.

2. **Validation**
    - `@Pattern` enforces a specific format for `accountNumber`.
    - Negative withdrawals are disallowed; attempting to withdraw more than the balance will throw an `IllegalArgumentException`.
    - Deposits, withdrawals, and transfers require the correct combination of source/target accounts.

3. **Transaction Types**
    - **DEPOSIT**: Must specify a target account.
    - **WITHDRAWAL**: Must specify a source account.
    - **TRANSFER**: Must specify both source and target accounts.

4. **In-Memory Storage**
    - Accounts are stored in a `ConcurrentHashMap` in `AccountService`.
    - Transactions are stored in an `ArrayList` in `LedgerService`.
    - Data resets when the application restarts.

5. **No Auth**
    - All operations are open to any caller for demonstration.
## Endpoints Overview

### 1. Account Controller

**Base Path**: `/api/accounts`

| Method | Path                   | Description                                       | Example |
|--------|------------------------|---------------------------------------------------|---------|
| POST   | `/api/accounts`        | Create a new account (optionally with an initial balance) | `/api/accounts?accountNumber=GB82WEST12345698765432&initialBalance=1000` |
| GET    | `/api/accounts/{acct}` | Retrieve a specific account                       | `/api/accounts/GB82WEST12345698765432` |
| GET    | `/api/accounts`        | Retrieve all accounts (map of accountNumber -> Account)    | `/api/accounts` |

### 2. Ledger Controller

**Base Path**: `/api/ledger`

| Method | Path                 | Description                                         | 
|--------|----------------------|-----------------------------------------------------|
| POST   | `/api/ledger/transactions` | Record a deposit, withdrawal, or transfer transaction | 
| GET    | `/api/ledger/transactions`  | Retrieve all transactions (read-only list)            | 
| GET    | `/api/ledger/balance`       | Show a *global* balance (if you kept that method)      | 

### Example JSON for a Transfer

```json
{
  "type": "TRANSFER",
  "amount": 250.00,
  "sourceAccount": {
    "accountNumber": "GB82WEST12345698765432"
  },
  "targetAccount": {
    "accountNumber": "GB02NWBK60161331926819"
  }
}
```

### Example JSON for a transaction
```json
{
  "type": "DEPOSIT",
  "amount": 100.00
}
```

## Design Strategy
* This application follows a typical Spring Boot layered architecture:
    * _Controller_ (LedgerController): Handles HTTP requests.
    * _Service_ (LedgerService): Encapsulates ledger logic (recording transactions, calculating balances).
    * _Model_ (Transaction, TransactionType): Domain objects representing ledger data.
    * _In-Memory storage_ (List<Transaction>): Returns an unmodifiable list to prevent external changes.

## How to Run Locally (Without Docker)
1. Clone this repository.
2. Build the project.
```shell
./gradlew clean build --no-daemon 
```
3. Run the application.
```shell
./gradlew bootRun
```
4. Visit `http://localhost:8080/` in your browser.

## How to Run Locally (With Docker)
1. Clone this repository.
2. Build the Docker image.
```shell
docker build -t tiny-ledger .
```
3. Run the Docker container.
```shell
docker run -p 8080:8080 --name tiny-ledger-container tiny-ledger
```
4. Visit `http://localhost:8080/` in your browser.
## How to Run with Docker Compose
1. Clone this repository.
2. Run the following command in the root directory of the project.
```shell
docker-compose up --build
```
3. Visit `http://localhost:8080/` in your browser.

## Testing
This project has two main test classes:
1. **Unit Tests**:
   * **LedgerServiceTest:** Verifies deposit, withdrawal, transfer operations, insufficient funds handling, and immutability of the transactions list.
   * **AccountServiceTest:** Checks account creation, balance updates (deposit and withdrawal), and error handling for duplicate or non-existent accounts.
2. **Integration/Slice Tests**:
   * **LedgerControllerTest:** Validates transaction recording via JSON requests, response status codes, and service interactions.
   * **AccountsControllerTest:** Tests account creation, retrieval, and listing endpoints.

To run the tests, execute the following command:
```shell
./gradlew test
```
## Sample Usage (cURL)
1. Create Two Accounts
```shell
curl -X POST "http://localhost:8080/api/accounts?accountNumber=GB82WEST12345698765432&initialBalance=500"
curl -X POST "http://localhost:8080/api/accounts?accountNumber=GB02NWBK60161331926819&initialBalance=300"
```
2. Transer 100 from Account1 to Account2
```shell
curl -X POST -H "Content-Type: application/json" \
-d '{
  "type": "TRANSFER",
  "amount": 100,
  "sourceAccount": {"accountNumber":"GB82WEST12345698765432"},
  "targetAccount": {"accountNumber":"GB02NWBK60161331926819"}
}' \
http://localhost:8080/api/ledger/transactions
```
3. Check All Transactions
```shell
curl http://localhost:8080/api/ledger/transactions
```
4. Check Updated Balances
```shell
curl http://localhost:8080/api/accounts/GB82WEST12345698765432
curl http://localhost:8080/api/accounts/GB02NWBK60161331926819
```