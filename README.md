# rest-redis-queue

## Requirements
- java 11+

## Endpoint
```text
GET http://localhost:8080/
```

## Build
```text
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=serviceOne -pl service-one -am
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=serviceOne -pl service-two -am
```

## Test
```shell script
mvn -B test -am
```