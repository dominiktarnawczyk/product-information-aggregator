# Product Information Aggregator

Gradle multi-module project with two independent Spring Boot applications:

- `aggregator` on port `8080`
- `mocks` on port `8081`

This service aggregates product information from multiple upstream services (Catalog, Pricing, Availability, and Customer services).

### GET `/api/products`

Aggregates product information from multiple services.

**Query Parameters:**
- `product-id` (required): The product identifier
- `market-language` (required): The market/language code (e.g., "en-GB", "de-DE")
- `customer-id` (optional): Customer identifier for personalized data

**Example Request:**
```bash
curl "http://localhost:8080/api/products?product-id=PROD001&market-language=en-GB&customer-id=CUST123"
```

**Success Response (200 OK):**
```json
{
  "catalog": {
    "name": "Product Name",
    "description": "Product Description",
    "specs": {
      "dimension": "10x20x30 cm",
      "weight": "2.5 kg",
      "material": "Plastic",
      "color": "Blue"
    },
    "images": ["image1.jpg", "image2.jpg"]
  },
  "pricing": {
    "bestPrice": "99.99 EUR",
    "customerDiscount": "10%",
    "finalPrice": "89.99 EUR"
  },
  "availability": {
    "stockLevel": 50,
    "warehouseLocation": "Warehouse A",
    "expectedDelivery": "2024-04-15"
  },
  "customer": {
    "customerSegment": "Premium",
    "preferences": {
      "communicationChannel": "Email",
      "newsletterSubscription": true,
      "loyaltyProgramMember": true
    }
  }
}
```

## Configuration

Service URLs are configured in `aggregator/application.yaml`:

```yaml
endpoints:
   catalog:
      url: http://localhost:8081/api/catalog
   pricing:
      url: http://localhost:8081/api/pricing
   availability:
      url: http://localhost:8081/api/availability
   customer:
      url: http://localhost:8081/api/customer
```

## Running the Application

### Prerequisites
- JDK 21
- Gradle 8.x

### Start Mock Services
```bash
./gradlew :mocks:bootRun
```

### Start Aggregator Service
```bash
./gradlew :aggregator:bootRun
```

The aggregator will be available at `http://localhost:8080`.

## Testing

### Test with all services working:
```bash
curl "http://localhost:8080/api/products?product-id=PROD001&market-language=en-GB&customer-id=CUST123"
```

### Test without customer ID:
```bash
curl "http://localhost:8080/api/products?product-id=PROD001&market-language=en-GB"
```

### Test with slow services:
The mock services have built-in latency and failure rates that can be configured via environment variables.


See [CHAOS_MONKEY_QUICKSTART.md](mocks/CHAOS_MONKEY_QUICKSTART.md) for detailed usage guide.

## Key decisions and trade-offs
- The mock services are implemented in a separate Spring Boot application to allow independent development and testing of the aggregator service.
- The mock services are using interceptors on endpoints to simulate latency and failures, which allows for easy configuration and testing of different scenarios, every endpoint separately.
- The mock services include configurable latency and failure rates to simulate real-world conditions, but the current implementation does not include advanced features like circuit breakers.
- The mock services can be easily extended to include additional endpoints or more complex behavior, but the current implementation focuses on simplicity for demonstration purposes.
- The aggregator service uses a simple REST client to call upstream services, which allows for easy integration and testing, but may not be the most efficient approach for high-throughput scenarios.
- The current implementation focuses on simplicity and clarity for demonstration purposes, rather than performance optimizations or advanced error handling.
- The error handling is basic, returning Service Unavailable status when any upstream service fails.
- The aggregator service has configurable timeouts for upstream service calls, but the current implementation does not include a retry mechanism.
- Basic ports and adapters approach makes it easy to test but also to swap out the REST client for a more efficient communication mechanism (e.g., gRPC or message-based communication) in the future if needed.

## Future Improvements
- Implement caching to reduce latency.
- Implement authentication and authorization.
- CI/CD pipeline for automated testing and deployment.
- Add monitoring and alerting for service health and performance.
- Migrate to gRPC for better performance.
- Add tracing and logging for better observability of request flows and failures.
- Use a circuit breaker pattern to handle downstream service failures more gracefully.
- Implement retry mechanism with exponential backoff for transient failures.
- Use Either (Arrow Kt) type for better error handling and propagation of failure reasons to the client.
- Implement a more comprehensive testing strategy, including integration tests that simulate various failure scenarios and edge cases.

## Design question - "The Assortment team wants to add a 'Related Products' service (200ms latency, 90% reliability). How would your design accommodate this? Should it be required or optional?"
To accommodate the addition of a "Related Products" service with 200ms latency and 90% reliability, I would design the aggregator service to treat this new service as optional. 
This means that if the "Related Products" service fails or is slow to respond, the aggregator can still return the main product information without it.
On mocks side, I would add a new endpoint for the "Related Products" service with the specified latency and reliability characteristics.
In the aggregator service I would add new provider by extending InfoProvider interface and implement the logic to call the "Related Products" service, but I would also implement a timeout and fallback mechanism to ensure that if the service is unavailable or slow, it does not impact the overall response time of the aggregator service.