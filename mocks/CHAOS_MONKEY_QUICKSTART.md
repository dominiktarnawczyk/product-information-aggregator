# API Interceptors - Quick Start Guide

Custom Spring interceptors for API endpoints that allow you to:
1. **Simulate latency** - Add random delays to API responses
2. **Simulate failures** - Randomly return error responses

## Configuration Reference

### Availability API

| Environment Variable               | Description                 | Default | Example     |
|------------------------------------|-----------------------------|---------|-------------|
| `AVAILABILITY_INTERCEPTOR_ENABLED` | Enable/disable interceptor  | `true`  | `true`      |
| `AVAILABILITY_LATENCY_MIN`         | Min latency in milliseconds | `50`    | `100`       |
| `AVAILABILITY_LATENCY_MAX`         | Max latency in milliseconds | `150`   | `500`       |
| `AVAILABILITY_FAILURE_RATE`        | Failure rate (0.0-1.0)      | `0.02`  | `0.2` (20%) |
| `AVAILABILITY_ERROR_STATUS_CODE`   | HTTP status for failures    | `500`   | `503`       |

### Catalog API

| Environment Variable           | Description                 | Default  | Example     |
|--------------------------------|-----------------------------|----------|-------------|
| `CATALOG_INTERCEPTOR_ENABLED`  | Enable/disable interceptor  | `true`   | `false`     |
| `CATALOG_LATENCY_MIN`          | Min latency in milliseconds | `50`     | `100`       |
| `CATALOG_LATENCY_MAX`          | Max latency in milliseconds | `50`     | `500`       |
| `CATALOG_FAILURE_RATE`         | Failure rate (0.0-1.0)      | `0.001`  | `0.2` (20%) |
| `CATALOG_ERROR_STATUS_CODE`    | HTTP status for failures    | `500`    | `503`       |

### Customer API

| Environment Variable            | Description                 | Default | Example     |
|---------------------------------|-----------------------------|---------|-------------|
| `CUSTOMER_INTERCEPTOR_ENABLED`  | Enable/disable interceptor  | `true`  | `false`     |
| `CUSTOMER_LATENCY_MIN`          | Min latency in milliseconds | `60`    | `100`       |
| `CUSTOMER_LATENCY_MAX`          | Max latency in milliseconds | `60`    | `500`       |
| `CUSTOMER_FAILURE_RATE`         | Failure rate (0.0-1.0)      | `0.01`  | `0.2` (20%) |
| `CUSTOMER_ERROR_STATUS_CODE`    | HTTP status for failures    | `500`   | `503`       |

## Availability API Examples

### Test with Latency (200-500ms)
```bash
AVAILABILITY_INTERCEPTOR_ENABLED=true \
AVAILABILITY_LATENCY_MIN=200 \
AVAILABILITY_LATENCY_MAX=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Should take 200-500ms to respond
time curl "http://localhost:8081/api/availability?product-id=TEST-123&market-code=en-GB"
```

### Test with 30% Failure Rate
```bash
AVAILABILITY_INTERCEPTOR_ENABLED=true \
AVAILABILITY_FAILURE_RATE=0.3 \
AVAILABILITY_ERROR_STATUS_CODE=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Run multiple times - about 30% should return 500 errors
for i in {1..10}; do
  echo "Request $i:"
  curl -w "\nHTTP Status: %{http_code}\n" "http://localhost:8081/api/availability?product-id=TEST-123&market-code=en-GB"
  echo "---"
done
```

## Catalog API Examples

### Test with Latency (200-500ms)
```bash
CATALOG_INTERCEPTOR_ENABLED=true \
CATALOG_LATENCY_MIN=200 \
CATALOG_LATENCY_MAX=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Should take 200-500ms to respond
time curl "http://localhost:8081/api/catalog?product-id=TEST-123&market-code=en-GB"
```

### Test with 30% Failure Rate
```bash
CATALOG_INTERCEPTOR_ENABLED=true \
CATALOG_FAILURE_RATE=0.3 \
CATALOG_ERROR_STATUS_CODE=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Run multiple times - about 30% should return 500 errors
for i in {1..10}; do
  echo "Request $i:"
  curl -w "\nHTTP Status: %{http_code}\n" "http://localhost:8081/api/catalog?product-id=TEST-123&market-code=en-GB"
  echo "---"
done
```

## Customer API Examples

### Test with Latency (200-500ms)
```bash
CUSTOMER_INTERCEPTOR_ENABLED=true \
CUSTOMER_LATENCY_MIN=200 \
CUSTOMER_LATENCY_MAX=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Should take 200-500ms to respond (without customer-id)
time curl "http://localhost:8081/api/customer?market-code=en-GB"

# Should take 200-500ms to respond (with customer-id)
time curl "http://localhost:8081/api/customer?market-code=en-GB&customer-id=CUSTOMER-456"
```

### Test with 30% Failure Rate
```bash
CUSTOMER_INTERCEPTOR_ENABLED=true \
CUSTOMER_FAILURE_RATE=0.3 \
CUSTOMER_ERROR_STATUS_CODE=500 \
./gradlew :mocks:bootRun
```

In another terminal:
```bash
# Run multiple times - about 30% should return 500 errors
for i in {1..10}; do
  echo "Request $i:"
  curl -w "\nHTTP Status: %{http_code}\n" "http://localhost:8081/api/customer?market-code=en-GB&customer-id=CUSTOMER-456"
  echo "---"
done
```

