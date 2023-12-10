# Booking Transaction Service
Given a list of booking transactions, returns the rejected transactions.

## Build and run
1. Start Docker
2. In the command line, in the directory for this application, run:
   1. ```./gradlew build``` to create a .jar file of the application
   1. ```docker-compose up -d``` to create and run a Docker image of the application and its database.
3. In Postman, send the following requests (Postman config available under /docs):
   1. Set Credit Limit - set an initial credit limit for all users that should be allowed to make booking transactions.
   1. Process Transactions - send in the booking transactions to be processed, and receive a list of the rejected transactions.