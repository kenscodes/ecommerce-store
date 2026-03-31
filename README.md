# E-commerce Store API

A Spring Boot-based e-commerce store with cart management, checkout functionality, and a discount system that rewards customers.

## Features

- **Product Management**: Browse available products
- **Cart Management**: Add, update, and remove items from cart
- **Checkout Process**: Place orders with optional discount codes
- **Discount System**: Every nth order generates a discount coupon
- **Admin Analytics**: View sales metrics and discount usage
- **In-memory Storage**: No database required for demo purposes

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Maven
- JUnit 5 for testing

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd ecommerce-store
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
mvn test
```

## API Endpoints

### Products

- `GET /api/products` - Get all products
- `GET /api/products/{productId}` - Get specific product

### Cart Management

- `GET /api/cart/{customerId}` - Get customer's cart
- `POST /api/cart/{customerId}/items` - Add item to cart
  ```json
  {
    "productId": "P001",
    "quantity": 2
  }
  ```
- `PUT /api/cart/{customerId}/items/{productId}?quantity=3` - Update item quantity
- `DELETE /api/cart/{customerId}/items/{productId}` - Remove item from cart
- `DELETE /api/cart/{customerId}` - Clear cart

### Checkout

- `POST /api/checkout` - Place order
  ```json
  {
    "customerId": "customer123",
    "discountCode": "SAVE10" // optional
  }
  ```
- `GET /api/checkout/order/{orderId}` - Get order details

### Admin APIs

- `POST /api/admin/discounts/generate` - Generate discount code (if conditions met)
- `GET /api/admin/discounts` - Get all discount codes
- `GET /api/admin/analytics` - Get store analytics
- `PUT /api/admin/settings/discount?everyNthOrder=5&percentage=15` - Update discount settings

## Sample Products

The application comes pre-loaded with sample products:

| ID | Name | Price | Stock |
|----|------|-------|-------|
| P001 | Laptop | $999.99 | 50 |
| P002 | Mouse | $29.99 | 100 |
| P003 | Keyboard | $79.99 | 75 |
| P004 | Monitor | $199.99 | 30 |
| P005 | Headphones | $149.99 | 40 |

## Discount System

The discount system works as follows:

1. **Default Settings**: Every 3rd order generates a 10% discount coupon
2. **Coupon Generation**: Admin can manually trigger coupon generation when conditions are met
3. **Coupon Usage**: Customers can apply discount codes during checkout
4. **One-time Use**: Each discount code can only be used once
5. **Configuration**: Admin can modify the nth order interval and discount percentage

## API Response Format

All APIs return responses in the following format:

```json
{
  "success": true,
  "message": "Success message",
  "data": { ... } // response data or null
}
```

## Error Handling

- **400 Bad Request**: Invalid input parameters
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server-side errors

## Example Usage

### 1. Add items to cart

```bash
curl -X POST http://localhost:8080/api/cart/customer123/items \
  -H "Content-Type: application/json" \
  -d '{"productId": "P001", "quantity": 2}'
```

### 2. View cart

```bash
curl http://localhost:8080/api/cart/customer123
```

### 3. Checkout with discount

```bash
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{"customerId": "customer123", "discountCode": "SAVE10"}'
```

### 4. View analytics

```bash
curl http://localhost:8080/api/admin/analytics
```

## Testing the Discount System

1. Place 2 orders (no discount generated yet)
2. Use the admin API to generate a discount for the 3rd order:
   ```bash
   curl -X POST http://localhost:8080/api/admin/discounts/generate
   ```
3. Place a 3rd order and apply the generated discount code
4. Check analytics to see discount usage

## Architecture

The application follows a layered architecture:

- **Controller Layer**: REST API endpoints
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access (in-memory storage)
- **Model Layer**: Domain entities
- **DTO Layer**: Data transfer objects for API requests/responses

## Development

### Project Structure

```
src/
├── main/java/com/ecommerce/store/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   ├── model/          # Domain entities
│   ├── dto/            # Data transfer objects
│   └── EcommerceStoreApplication.java
└── test/java/com/ecommerce/store/
    ├── service/        # Service layer tests
    ├── controller/     # Controller tests
    └── repository/     # Repository tests
```

### Adding New Features

1. Define domain models in the `model` package
2. Create DTOs for API requests/responses
3. Implement business logic in services
4. Add REST endpoints in controllers
5. Write unit tests

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is for demonstration purposes only.
