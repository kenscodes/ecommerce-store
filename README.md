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

## Web Interface Testing

The application includes a comprehensive web UI for testing all APIs without needing tools like Postman.

### Access the Web Interface

Open your browser and navigate to: **http://localhost:8080**

### UI Layout Description

#### **Header Section**
- **Title**: "🛒 E-commerce Store API Demo"
- **Subtitle**: "Test the REST APIs with a real-time interface - In-memory storage shared across all users"

#### **Main Content Area** (Two-column layout)

**Left Column - Products Section**
- **Header**: "📦 Products" with product count badge
- **Product Grid**: 5 sample products displayed as cards:
  - **Laptop** - $999.99 (Stock: 50)
  - **Mouse** - $29.99 (Stock: 100) 
  - **Keyboard** - $79.99 (Stock: 75)
  - **Monitor** - $199.99 (Stock: 30)
  - **Headphones** - $149.99 (Stock: 40)
- Each product card shows: name, price, stock quantity, "Add to Cart" button
- Out-of-stock items show disabled "Out of Stock" button

**Right Column - Cart Section**
- **Header**: "🛒 Shopping Cart" with item count badge
- **Cart Items**: Shows added products with quantity controls
- **Cart Summary**: Displays subtotal, discount (if applied), and total
- **Discount Input**: Text field for discount codes with "Apply Discount" button
- **Checkout Button**: "🚀 Checkout" button (disabled when cart is empty)

#### **Admin Analytics Section** (Bottom, full-width)

**Control Panels** (Two-column layout)

**Left Panel - Discount Generation**
- **Header**: "🎫 Discount Generation"
- **Generate Button**: "🎫 Generate Discount Code" (blue gradient button)
- **Result Display**: Shows generated codes or error messages

**Right Panel - Discount Settings**
- **Header**: "⚙️ Discount Settings"
- **Input Fields**: 
  - "Every Nth order" (default: 3)
  - "Discount %" (default: 10%)
- **Update Button**: "Update Settings" (green button)
- **Result Display**: Success/error messages

**Discount Codes List**
- **Header**: "🎟️ All Discount Codes"
- **Refresh Button**: "🔄 Refresh Codes"
- **Codes Display**: Each code shows:
  - Code value (e.g., "DISCOUNT99625")
  - Percentage discount
  - Creation date/time
  - Status badge: "AVAILABLE" (green) or "USED" (red)

**Analytics Dashboard** (8 cards grid)
- **📊 Total Orders**: Number of completed orders
- **📦 Items Sold**: Total items purchased
- **💰 Revenue**: Total sales amount
- **🎫 Discount Codes**: Total codes generated
- **✅ Used Discounts**: Codes that have been applied
- **💸 Total Discounts**: Total discount amount given
- **⚙️ Discount Every**: Shows "1st/2nd/3rd/etc. order"
- **🎯 Discount Rate**: Current discount percentage

### UI Testing Guide

#### 🛒 **Product Browsing & Cart Management**

1. **View Products**
   - Products display in a grid with name, price, and stock
   - Real-time stock updates after purchases
   - Out-of-stock items are disabled

2. **Add to Cart**
   - Click "Add to Cart" on any product
   - Cart updates immediately with item count badge
   - Stock decreases in real-time for all users

3. **Cart Operations**
   - **View Cart**: Right panel shows all cart items
   - **Update Quantity**: Use +/- buttons to change quantities
   - **Remove Items**: Quantity goes to 0 removes item automatically
   - **Clear Cart**: Checkout automatically clears the cart

#### 🎫 **Discount System Testing**

1. **Generate Discount Codes**
   - Scroll to "Admin Analytics" section
   - Click "🎫 Generate Discount Code" button
   - **Success**: Shows generated code (e.g., "DISCOUNT12345")
   - **Error**: "No discount code generated. Current order count does not meet the criteria."

2. **Configure Discount Settings**
   - In "Discount Settings" panel:
   - Change "Every Nth order" (default: 3)
   - Change "Discount %" (default: 10%)
   - Click "Update Settings" to apply changes

3. **Apply Discount at Checkout**
   - Add items to cart
   - Enter discount code in the input field
   - Click "Apply Discount"
   - **Success**: Shows discount amount and updated total
   - **Error**: "Invalid discount code" or "This discount code has already been used"

4. **View All Discount Codes**
   - Click "🔄 Refresh Codes" button
   - See list with:
     - Code value and percentage
     - Creation date/time
     - Usage status (AVAILABLE/USED)
     - Used date (if applicable)

#### 📊 **Analytics Dashboard**

The admin section displays real-time metrics:
- **Total Orders**: Number of completed orders
- **Items Sold**: Total items purchased
- **Revenue**: Total sales amount
- **Discount Codes**: Total codes generated
- **Used Discounts**: Codes that have been applied
- **Total Discounts**: Total discount amount given
- **Discount Every**: Shows "1st/2nd/3rd/etc. order"
- **Discount Rate**: Current discount percentage

#### 🧪 **Multi-User Testing**

1. **Open Multiple Browser Tabs** to simulate multiple users
2. **Shared Inventory**: Stock updates affect all users
3. **Real-time Analytics**: Orders from any user appear immediately
4. **Shared Discount Codes**: Same codes available to all users
5. **Race Conditions**: Test simultaneous discount usage

#### 🎯 **Complete Testing Workflow**

**Scenario 1: Basic Purchase**
1. Add laptop to cart
2. Proceed to checkout (no discount)
3. Verify cart clears and analytics update

**Scenario 2: Discount Usage**
1. Generate discount code (if eligible)
2. Add items to cart
3. Apply discount code
4. Checkout with discount
5. Try to reuse same code (should show error)

**Scenario 3: Configuration Testing**
1. Change discount settings to "Every 2nd order" and "25%"
2. Place orders to test new configuration
3. Verify analytics show updated settings

**Scenario 4: Stock Management**
1. Add items until stock reaches 0
2. Verify "Out of Stock" button appears
3. Check stock updates across multiple browser tabs

### API Testing (Alternative)

If you prefer API testing, use these endpoints:

```bash
# View products
curl http://localhost:8080/api/products

# Add to cart
curl -X POST http://localhost:8080/api/cart/customer123/items \
  -H "Content-Type: application/json" \
  -d '{"productId":"P001","quantity":2}'

# View cart
curl http://localhost:8080/api/cart/customer123

# Checkout
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer123"}'

# Generate discount
curl -X POST http://localhost:8080/api/admin/discounts/generate

# View analytics
curl http://localhost:8080/api/admin/analytics
```

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
