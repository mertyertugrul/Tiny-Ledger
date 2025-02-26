# Tiny Ledger

* A simple in-memory ledger application built with Spring Boot (Java 17). It supports:
  - Recording deposits and withdrawals
  - Viewing the current balance
  - Viewing transaction history

* The data is stored **in-memory**, so it resets when the application restarts.

## Assumptions
1. **Single Account:** This application manages a single global ledger; no per-user logic is included.
2. **In-Memory Storage:** Transactions and balances vanish on restart. No external database is used.
3. **Minimal Validation:** We do not prevent negative deposit amounts, overdrafts, etc. It is assumed the user will provide sensible inputs.
4. **No Authentication:** All endpoints are publicly accessible for demonstration purposes.

## Endpoints

| HTTP Method | Path             | Description                          | Sample Payload             |
|-------------|------------------|--------------------------------------|----------------------------|
| **POST**    | `/api/transactions` | Record a deposit/withdrawal transaction | `{ "type": "DEPOSIT", "amount": 100.00 }` |
| **GET**     | `/api/balance`      | Retrieve the current balance        | _None_                     |
| **GET**     | `/api/transactions` | Retrieve all transactions           | _None_                     |

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
1. **LedgerServiceTest** – Unit tests for the business logic:
   * Validates deposit, withdrawal, current balance, and transaction list immutability.
2. **LedgerControllerTest** – Integration/slice tests with MockMvc:
   * Validates JSON request/response, status codes, and correct interactions with the service layer.
* To run the tests, execute the following command:
```shell
./gradlew test
```
## Sample Usage (cURL)
1. Deposit
```shell
curl -X POST -H "Content-Type: application/json" \
-d '{"type":"DEPOSIT","amount":100.50}' \
http://localhost:8080/api/transactions 
```
2. Withdraw
```shell
curl -X POST -H "Content-Type: application/json" \
-d '{"type":"WITHDRAWAL","amount":50}' \
http://localhost:8080/api/transactions
```
3. Check Balance
```shell
curl http://localhost:8080/api/balance
```
4. Transaction History
```shell
curl http://localhost:8080/api/transactions
```