# product-information-aggregator

Gradle multi-module project with two independent Spring Boot applications:

- `aggregator` on port `8080`
- `mocks` on port `8081`

## Requirements

- Java 21

## Run apps

Run `aggregator`:

```bash
./gradlew :aggregator:bootRun
```

Run `mocks`:

```bash
./gradlew :mocks:bootRun
```

Run both in separate terminals:

```bash
./gradlew :aggregator:bootRun
./gradlew :mocks:bootRun
```
