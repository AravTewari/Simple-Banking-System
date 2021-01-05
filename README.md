# Simple Banking System
Code which asks user to create a card or sign in. When creating a card, it will create a card number which passes the Luhn algorithm and generates a random 4-digit pin. Once created, the information will be stored in a local sqlite database. This ensures that multiple cards can be created and stored, thus creating a "banking system." The user can log in using the information given to them and will check if the table contains that information. If it does, the user will be prompted with a second menu ranging from adding and transfering income to logging out. Everything the user does on their account gets updated to the database.

*Main.java of the final task can be found in task/src/banking/Main.java*
