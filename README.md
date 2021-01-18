# MarkLogic and Data Hub Alstom scheduler

This application will allow you scehdule workflow on Alstom marklogic datahub

## Install

* 1 - install lombok plugin for vs code
* 2 - replace the datahub path in the application.yml
* 3 - use main gradle task (build / clean / run)
* 4 - you can use either gradle run or java -jar build/libs/alstom-scheduler.jar


## Configuration Properties 

application.yml

## Development 
This project utilizes Spring Boot and MarkLogic Libraries to achive integrations. Please adhere to the best practices. 

Spring functionnalities used on this project:
- scheduler
- Async
- spring boot for an easy configuration