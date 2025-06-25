# Melita Order Taking API

This is a Spring Boot-based authenticated Order Taking API designed for 3rd party ERP systems to place and track customer orders. Orders are validated, persisted, and published to a Kafka topic for downstream systems (e.g., fulfillment, care).

---
## Features

- RESTful endpoints to place and retrieve orders
- OAuth2-based token generation (Identity Service)
- Idempotency support for order submissions
- Kafka event publishing
- Input validation (with JSR-380/Bean Validation)
- Externalized configuration via Spring Cloud Config + Vault

---

## Microservices

| Service            | Port | Description                        |
|--------------------|------|------------------------------------|
| `order-service`    | 8081 | Handles order creation & retrieval |
| `identity-service` | 8082 | Handles token authentication       |
| `order-fulfilment` | 8083 | Dummy Kafka consumer               |
| `api-gateway`      | 8085 | Gateway for routing/authentication |
| `config-server`    | 8888 | Centralized configuration via Vault |

---

## Installation

### 1. **Clone the repository**

```bash
git clone https://github.com/SammyOcharo/melita-order-system.git  
cd melita-order-api

```
or git

```bash
git clone git@github.com:SammyOcharo/melita-order-system.git 
cd melita-order-api

```

### 2. **Start MYSQL and Kafka services**
Both mysql and Kafka I opted for local use. 
for Mysql ensure you have installation for mysql if not install via https://dev.mysql.com/downloads/installer/   

for Kafka download Apache Kafka from official website https://kafka.apache.org/

When kafka zip folder is downloaded. Unzip then 

````bash
cd kafka

````
you will be able to see a bin folder.

We need to run two commands.

First is to start zookeeper then the next is to start Kafka as kafka depends 
on zookeeper.

```` bash
./bin/zookeeper-server-start.sh config/zookeeper.properties
````
Then run command to start kafka
```` bash
 ./bin/kafka-server-start.sh config/server.properties
 ````

### 3. **Start Vault and put secrets**
starting vault on local use command

````bash
vault server -dev

````
Then get the token that is generated on the terminal
pass it as an export value as below

````bash
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=root
````

#### Setting vault secrets.  
```` bash
vault kv put secret/order-api-service \
  spring.datasource.url="jdbc:mysql://localhost:3306/orderdb" \
  spring.datasource.username="put_your_mysql_username" \
  spring.datasource.password="put_your_mysql_password" \
  spring.datasource.driver-class-name="com.mysql.cj.jdbc.Driver" \
  spring.jpa.hibernate.ddl-auto="update" \
  spring.jpa.database-platform="org.hibernate.dialect.MySQLDialect"
````
```` bash
vault kv put secret/identity-service \
  server.port=8082 \
  spring.application.name=IDENTITY-SERVICE \
  spring.security.user.name=user \
  spring.security.user.password=password \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-id=oidc-client \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-secret={noop}secret \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-authentication-methods=client_secret_basic \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.authorization-grant-types=authorization_code,refresh_token,client_credentials \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.redirect-uris=http://127.0.0.1:8080/login/oauth2/code/oidc-client \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.post-logout-redirect-uris=http://127.0.0.1:8080/ \
  spring.security.oauth2.authorizationserver.client.oidc-client.registration.scopes=openid,profile \
  spring.security.oauth2.authorizationserver.client.oidc-client.require-authorization-consent=true
````
```` bash
vault kv put secret/api-gateway \
  spring.cloud.gateway.routes[0].id=order-service \
  spring.cloud.gateway.routes[0].uri=http://localhost:8081 \
  spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/orders/** \
  spring.cloud.gateway.routes[1].id=identity-service \
  spring.cloud.gateway.routes[1].uri=http://localhost:8082 \
  spring.cloud.gateway.routes[1].predicates[0]=Path=/auth/** \
  server.port=8085 \
  order.service.create-url=http://localhost:8081/api/v1/orders/create-order \
  nimbus.jwk.setUri=http://127.0.0.1:8082/oauth2/jwks
````
Our configurations are done.

Then next step is to start our microservices.

```` bash
cd config-server && ./mvnw spring-boot:run

````
```` bash
cd order-service && ./mvnw spring-boot:run
````
```` bash
cd identity-service && ./mvnw spring-boot:run
````
```` bash
cd api-gateway && ./mvnw spring-boot:run
````
```` bash
cd order-fulfilment-service && ./mvnw spring-boot:run
````

### 4. **Testing**
The first API to test is the authentication api. The api gives a token 
That can use to make an authenticated request to create an order.

Here is /auth api.

127.0.0.1:8082/oauth2/token 

Post request. 

The token generation is implemented with the default spring Authorization server.

The api gives a response of 

```bash
{
    "access_token": "eyJraWQiOiI2NmEzMzFhZC1lYmJmLTQ5YmQtOTZjZS00OTRkMjlhNDI4MGUiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJvaWRjLWNsaWVudCIsImF1ZCI6Im9pZGMtY2xpZW50IiwibmJmIjoxNzUwODQyMDQ5LCJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwODIiLCJleHAiOjE3NTA4NDIzNDksImlhdCI6MTc1MDg0MjA0OSwianRpIjoiMmVkZjJjYzItZjIzZi00OTY2LTg1MzctNzg4MTU3ZTFhYmNmIn0.kMiqrmMWp9SjHFs4saoFrLdHPwgsR9fh4NQZm4y_oZfNsnEw9eZi2sqHEbj4AGb6uKYKxlwBEZ8cUwJ-yrtyAGm1VNnP9HCjZNi4rVSOSe-XqnPA6X6RJBkLwdgsz_iNXWOkA5I6ccMoG2Oso6sEGSBTxzQiyJrF4cwxlqsK0jtjz9io5BJV_0R3qo8Xy3uTUFJdPlqCXUPXU6mvtlvrwEq2g9uqFa274jn2yp-GH45V12Z20MQNM6CULsrRtISwW8_KcYTIkfPlPBmsDMPTSN2wxTuGQEbvL2yeokqcV2ZvKlW-C9HMAqK4uS4Tw4dR8tr46qpyl6m0hBWEfp5Ifg",
    "token_type": "Bearer",
    "expires_in": 299
}
```
Then we can use the access token to make the creation order request.

127.0.0.1:8085/api/v1/orders/create-order

Post request payload

```bash
{
  "customerName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+254712345678",
  "installationAddress": "123 Juja Street, Nairobi",
  "preferredDate": "2025-06-26",
  "preferredTimeSlot": "10:00 AM - 12:00 PM",
  "products": [
    {
      "productType": "INTERNET",
      "packageName": "Internet 270Mbps"
    }
  ]
}

```

The response of successful will be 
```bash
{
    "id": "1f5809ca-5f27-40dc-97d5-633f64edb5a3",
    "orderNo": "ORD-20250625-250EF4",
    "customerName": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+254712345678",
    "installationAddress": "123 Juja Street, Nairobi",
    "preferredDate": "2025-06-26",
    "preferredTimeSlot": "10:00 AM - 12:00 PM",
    "products": [
        {
            "productType": "INTERNET",
            "packageName": "Internet 270Mbps"
        }
    ],
    "status": "PENDING"
}
```
with a response code of 200

Additionally, the kafka producer has published a message to the topic and using our implementation
order-fulfilment-service to act as a consumer.


```bash
Received order message: {"orderId":"1f5809ca-5f27-40dc-97d5-633f64edb5a3","email":"john.doe@example.com","preferredDate":[2025,6,26],"timeSlot":"10:00 AM - 12:00 PM","status":"PENDING","items":[{"productType":"INTERNET","packageName":"Internet 270Mbps"}],"eventVersion":"v1"}
```