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

## Testing with API Interceptors

The `mocks` application includes API interceptors (chaos engineering features) that allow you to simulate latency and failures for testing purposes.

See [CHAOS_MONKEY_QUICKSTART.md](mocks/CHAOS_MONKEY_QUICKSTART.md) for detailed usage guide.

