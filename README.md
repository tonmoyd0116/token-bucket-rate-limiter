# Token Bucket Rate Limiter

A production-ready **Token Bucket Rate Limiter** built using **Java 21**, **Spring Boot 3**, and **Redis**. This project demonstrates how to efficiently control API request rates while allowing short bursts of traffic, making it suitable for real-world distributed systems.

---

## 📌 Overview

A Token Bucket Rate Limiter is a popular rate-limiting algorithm used to protect APIs and backend services from excessive traffic. It allows clients to make requests as long as tokens are available in their bucket. Tokens are replenished at a fixed rate over time.

Each incoming request:

* Consumes one token from the client's bucket.
* Is allowed if a token is available.
* Is rejected with **HTTP 429 (Too Many Requests)** if no tokens remain.

Unlike fixed-window rate limiting, the Token Bucket algorithm supports controlled bursts while enforcing a long-term request rate.

---

## 🚀 Features

* Token Bucket rate-limiting algorithm
* Configurable bucket capacity
* Configurable refill rate
* Redis-backed storage for distributed deployments
* Thread-safe implementation
* Spring Boot REST API
* Easy to extend for API Gateway integration

---

## 🏗️ Tech Stack

* Java 21
* Spring Boot 3
* Spring Data Redis
* Redis
* Maven
* Lombok

---

## 📖 How Token Bucket Works

Each client has an associated bucket containing a fixed number of tokens.

```
Capacity = 20 tokens
Refill Rate = 10 tokens/second
```

Initially:

```
********************
20 Tokens Available
```

When requests arrive:

```
Request

↓

Token Available?

↓

YES ------------------> Consume Token

↓

Process Request

NO

↓

Return HTTP 429
```

Example:

```
Bucket Capacity = 5

★★★★★

Request 1

★★★★☆

Request 2

★★★☆☆

...

□□□□□

Next Request

❌ Rejected (429 Too Many Requests)
```

---

## 🔄 Token Refill

Instead of running a scheduled task every second, the implementation calculates the number of tokens that should have been generated since the last request.

Example:

```
Last Refill Time = 12:00:00
Current Time     = 12:00:03

Elapsed Time = 3 seconds

Refill Rate = 5 tokens/sec

Tokens to Add = 15
```

```
newTokens = min(capacity, currentTokens + generatedTokens)
```

This approach is lightweight and highly scalable.

---

## 🧱 Bucket State

Each bucket stores:

```
capacity
availableTokens
lastRefillTimestamp
```

Example Java model:

```java
public class Bucket {

    private long capacity;

    private long availableTokens;

    private long refillRate;

    private long lastRefillTimestamp;
}
```

---

## 🌐 Distributed Architecture

```
                 Client
                    │
                    ▼
             Spring Boot API
                    │
                    ▼
            Token Bucket Service
                    │
                    ▼
                 Redis
                    │
        Bucket State per Client
```

Redis stores:

```
clientId

tokens = 12

lastRefill = 1721150000
```

Using Redis enables multiple application instances to share the same rate-limiting state.

---

## ⚙️ Algorithm

```
Receive Request

↓

Locate Client Bucket

↓

Refill Tokens

↓

Token Available?

↓

YES
│
├── Consume Token
│
└── Allow Request

NO
│
└── Return HTTP 429
```

---

## 📂 Project Structure

```
src
├── controller
├── service
├── helper
├── model
├── config
├── repository
└── TokenBucketRateLimiterApplication.java
```

---

## ▶️ Running the Application

### Clone the repository

```bash
git clone https://github.com/<your-username>/token-bucket-rate-limiter.git
cd token-bucket-rate-limiter
```

### Start Redis

Using Docker:

```bash
docker run -d -p 6379:6379 redis
```

Or use a locally installed Redis server.

### Build

```bash
mvn clean install
mvn clean install -U "-Dmaven.skip.test=true"
```

### Run

```bash
mvn spring-boot:run
```

Or

```bash
java -jar target/token-bucket-rate-limiter.jar
```

---

## ⚙️ Configuration

Example:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60000

server.port=8443
# for logging purpose if the dev profile is active
spring.application.name=DEV_PROFILE

# Rate Limiting Properties
rate.limit.capacity=20
rate.limit.refill-rate=10


```

---

## 📌 Use Cases

This implementation can be used in:

* API Gateways
* Login throttling
* OTP generation
* Payment APIs
* AI/LLM APIs
* File upload services
* Public REST APIs
* Microservices communication
* Web scraping protection

---

## ⏱️ Time Complexity

Per request:

```
Time Complexity : O(1)

Space Complexity : O(n)
```

Where **n** is the number of active clients.

---

## 📈 Advantages

* Supports traffic bursts
* Smooth request throttling
* Stateless API layer
* Distributed using Redis
* High throughput
* Low latency
* Prevents abuse and DoS attacks
* Production-ready design

---

## 🔮 Future Enhancements

* Sliding Window implementation
* Leaky Bucket implementation
* Redis Lua scripting for atomic updates
* Dynamic rate limits per user or API
* API key-based configuration
* Metrics with Micrometer & Prometheus
* Grafana dashboard
* Spring Cloud Gateway integration

---

## 📚 References

* RFC 2697 – A Single Rate Three Color Marker
* Redis Documentation
* Spring Boot Documentation

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.
