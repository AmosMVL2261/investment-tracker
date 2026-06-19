# Investment Tracker API

A RESTful API for tracking and monitoring personal investments in stocks, cryptocurrencies, and ETFs. Built with Java and Spring Boot.

## Features

- JWT-based authentication (register and login)
- Portfolio management (add, update, delete, and view positions)
- Real-time asset prices via Alpha Vantage API
- Automatic profit/loss calculation per position and overall portfolio
- Price caching to optimize external API usage
- Global exception handling with descriptive error responses
- API documentation with Swagger UI

## Tech Stack

- **Java 21**
- **Spring Boot 4**
- **Spring Security** with JWT
- **PostgreSQL 16**
- **Hibernate / JPA**
- **Docker & Docker Compose**
- **Swagger / OpenAPI 3**

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- Alpha Vantage API key (free tier available at [alphavantage.co](https://www.alphavantage.co/support/#api-key))

### Running the project

1. Clone the repository:
<!-- bash -->
```
git clone https://github.com/AmosMVL2261/investment-tracker.git
cd investment-tracker
```

2. Create a `.env` file in the root directory:
```
DB_NAME=investmenttracker
DB_USER=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_base64_encoded_secret
ALPHAVANTAGE_API_KEY=your_api_key
ALPHAVANTAGE_BASE_URL=https://www.alphavantage.co/query
```

3. Start the application:
<!-- bash -->
```
docker compose up --build
```

4. Access the API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Auth
| Method | Endpoint | Description | Auth required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register a new user | No |
| POST | `/auth/login` | Login and receive JWT token | No |

### Portfolio
| Method | Endpoint | Description | Auth required |
|--------|----------|-------------|---------------|
| GET | `/portfolio` | Get full portfolio summary with P&L | Yes |
| GET | `/portfolio/{id}` | Get a specific portfolio entry | Yes |
| POST | `/portfolio` | Add an asset to the portfolio | Yes |
| PUT | `/portfolio/{id}` | Update a portfolio entry | Yes |
| DELETE | `/portfolio/{id}` | Remove an asset from the portfolio | Yes |

### Assets
| Method | Endpoint | Description | Auth required |
|--------|----------|-------------|---------------|
| GET | `/assets` | List all assets in the system | Yes |
| GET | `/assets/{symbol}` | Get an asset by symbol | Yes |

### Prices
| Method | Endpoint | Description | Auth required |
|--------|----------|-------------|---------------|
| GET | `/prices/{symbol}` | Get current market price for a symbol | Yes |

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer your_jwt_token
```

You can obtain a token by registering or logging in. The Swagger UI includes an **Authorize** button to set the token for all requests.

## Important Notes

- This is a **manual investment tracker**. It does not connect to any real brokerage or exchange account.
- Asset symbols are validated against the Alpha Vantage API. Only real market symbols are accepted.
- The free tier of Alpha Vantage allows 25 requests per day. Price caching is implemented to optimize usage.

## Future Plans (Phase 2)

- Transaction history (buy/sell records per position)
- Portfolio metrics (best performing asset, distribution by type)
- More comprehensive integration tests