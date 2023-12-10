# Booking Transaction Service
Given a list of booking transactions, returns the rejected transactions. Test assignment for the eBerry Omni team.

Credit limits are stored in MongoDB, and are updated for each transaction that is not rejected.

## Build and run
1. Start Docker
2. In the command line, in the directory for this application, run:
   1. ```./gradlew build``` to create a `.jar` file of the application
   1. ```docker-compose up -d``` to create and run a Docker image of the application and its database.
3. In Postman, send the following requests (using the Postman config available under `/docs`):
   1. `Set Credit Limits` - set an initial credit limit for all users that should be allowed to make booking transactions.
   1. `Process Transactions` - send in the booking transactions to be processed, and receive a list of the rejected transactions.

## Assumptions
* There must be a mistake in the instructions where it says "When the available limit exceeds the booking cost, the transaction cannot be successful", it should actually be "When the booking cost exceeds the available limit".
* When the instructions say to use Spring Boot 2, they must be outdated, so I used a more modern version of Spring Boot (3.2), since version 2 is not freely supported anymore.

## Shortcuts taken
* No security
* Not much validation or error handling
* No check if a transaction id is reused
* No default credit limit for new users
* Same data types used in both database and API