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
- Mock services running on port 8081

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

