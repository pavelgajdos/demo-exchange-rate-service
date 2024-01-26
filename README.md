# demo-exchange-rate-service

## How to run

- Just run `gradle bootRun` in the top directory or just run the application in IDEA
- The demo uses H2 database stored in a file to preserve data (fixer.io has very limited requests per month and using H2 in-memory would cause the depletion of the limit quickly)

>**Note:** Feel free to delete the db file to test data loading from the API. Just do not forget to change the property `spring.jpa.hibernate.ddl-auto` to `update`. 

## TODO
- use @NonNullApi
- add tests