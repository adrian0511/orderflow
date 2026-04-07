# 🚀 OrderFlow – Plataforma de Pedidos con Microservicios y Eventos

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=for-the-badge&logo=spring)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Driven-black?style=for-the-badge&logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)
![OAuth2](https://img.shields.io/badge/OAuth2-Authorization%20Code-purple?style=for-the-badge&logo=oauth)
![MailHog](https://img.shields.io/badge/MailHog-Email%20Testing-yellow?style=for-the-badge&logo=maildotru)

## 🌟 Proyecto

**OrderFlow** es una arquitectura de microservicios diseñada para gestionar pedidos en un e‑commerce moderno.  
Combina lo mejor del ecosistema Spring Cloud con eventos asíncronos (Kafka), seguridad OAuth2 con flujo **Authorization
Code**, resiliencia con Circuit Breaker y notificaciones por correo electrónico usando **MailHog**.

✅ Registro y autenticación de usuarios (Auth Server + JWT + Authorization Code)  
✅ Catálogo de productos con control de stock  
✅ Creación de pedidos con reserva de inventario  
✅ Procesamiento de pagos simulado (éxito/fallo aleatorio)  
✅ Notificaciones por correo con MailHog  
✅ Gateway unificado con balanceo de carga y Circuit Breaker  
✅ Documentación automática con OpenAPI (Swagger)  
✅ Descubrimiento de servicios con Eureka  
✅ Mensajería asíncrona con Kafka (eventos entre servicios)

---

## 🏗️ Arquitectura (Diagrama de Alto Nivel)

```
[Cliente] → [API Gateway (8080)] → [Servicios]
↓                        ↓
[Eureka Server]         [Kafka + MailHog]
↓                        ↓
[Auth] [User] [Product] [Inventory] [Order] [Payment] [Notification]
```

| Servicio               | Puerto (Eureka) | Descripción                                                  |
|------------------------|-----------------|--------------------------------------------------------------|
| `discovery-server`     | 8761            | Registro de servicios (Eureka)                               |
| `api-gateway`          | 8080            | Punto de entrada único, cliente OAuth2, valida JWT, balanceo |
| `auth-server`          | aleatorio       | Authorization Server (emite JWT), registro de usuarios       |
| `user-service`         | aleatorio       | Perfil de usuarios (consume evento `UserCreated`)            |
| `product-service`      | aleatorio       | CRUD de productos (solo admin)                               |
| `inventory-service`    | aleatorio       | Stock, reserva, confirmación, liberación                     |
| `order-service`        | aleatorio       | Orquestador de pedidos, reserva stock, eventos               |
| `payment-service`      | aleatorio       | Simula pagos, publica `payment-completed/failed`             |
| `notification-service` | aleatorio       | Envía correos (bienvenida, éxito/fallo pago)                 |

---

## 🛠️ Tecnologías Utilizadas

| Área               | Stack                                                                    |
|--------------------|--------------------------------------------------------------------------|
| **Lenguaje**       | Java 21                                                                  |
| **Framework**      | Spring Boot 4.0.5 (Spring MVC, WebFlux, Data JPA, Security, Cloud)       |
| **Microservicios** | Spring Cloud Gateway, Eureka, OpenFeign, Resilience4J                    |
| **Seguridad**      | Spring Security + OAuth2 Authorization Server + JWT (Authorization Code) |
| **Mensajería**     | Apache Kafka (eventos: `user-created`, `order-confirmed`, etc.)          |
| **Correos**        | JavaMailSender + MailHog + Thymeleaf                                     |
| **Bases de datos** | H2 (en memoria) – fácil para desarrollo                                  |
| **Documentación**  | SpringDoc OpenAPI (Swagger UI)                                           |
| **Contenedores**   | Docker + Docker Compose (Kafka, Zookeeper) + Docker Run (MailHog)        |

---

## 🚀 Cómo Levantar el Proyecto

### Requisitos Previos

- Java 21 (JDK 21)
- Docker y Docker Compose
- Maven

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/adrian0511/orderflow.git
cd orderflow
```

### Paso 2: Levantar la Infraestructura

#### 2.1 Kafka y Zookeeper con Docker Compose

Crea un archivo `docker-compose.yml` en la raíz del proyecto con el siguiente contenido (o utilizar el que viene en el
repo):

```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
    ports:
      - "9092:9092"
```

Luego ejecuta:

```bash
docker-compose up -d
```

#### 2.2 MailHog con Docker Run (SMTP falso + UI web)

```bash
docker run -d --name mailhog -p 1025:1025 -p 8025:8025 mailhog/mailhog:latest
```

#### 2.3 Verificar que todo esté corriendo

```bash
docker ps | grep -E "kafka|zookeeper|mailhog"
```

Accede a MailHog UI: [http://localhost:8025](http://localhost:8025)

### Paso 3: Compilar y Ejecutar los Microservicios

```bash
# Ejecutar cada servicio (en terminales separadas o en segundo plano)
cd discovery-server && mvn spring-boot:run &
cd ../auth-service && mvn spring-boot:run &
cd ../user-service && mvn spring-boot:run &
cd ../product-service && mvn spring-boot:run &
cd ../inventory-service && mvn spring-boot:run &
cd ../order-service && mvn spring-boot:run &
cd ../payment-service && mvn spring-boot:run &
cd ../notification-service && mvn spring-boot:run &
cd ../api-gateway && mvn spring-boot:run &
```

> **Nota:** Todos los servicios se registran en Eureka (puerto 8761). El Gateway escucha en el puerto 8080 y actúa como
> cliente OAuth2.

### Paso 4: Probar el Flujo Completo

#### 4.1 Registrar un Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"juan","email":"juan@mail.com","password":"123456"}'
```

#### 4.2 Obtener un Token JWT (Authorization Code) con Postman

- Crea una petición GET a
  `http://localhost:8080/oauth2/authorize?response_type=code&client_id=gateway-client&redirect_uri=http://localhost:8080/login/oauth2/code/gateway-client&scope=openid%20profile`
- En la pestaña **Authorization**, elige **OAuth 2.0** y haz clic en **Get New Access Token**.
- Configura:
    - Grant Type: `Authorization Code`
    - Auth URL: `http://localhost:8080/oauth2/authorize`
    - Access Token URL: `http://localhost:8080/oauth2/token`
    - Client ID: `gateway-client`
    - Client Secret: `secret`
    - Scope: `openid profile`
    - Callback URL: `http://localhost:8080/login/oauth2/code/gateway-client`
- Haz clic en **Request Token**, inicia sesión con el usuario registrado (`juan` / `123456`) y acepta el consentimiento.
- Postman guardará el token automáticamente.

#### 4.3 Crear un Producto (solo ADMIN – necesitas un token con rol ADMIN)

Primero asigna el rol ADMIN a un usuario en la base de datos de `auth-service` (o crea un usuario con rol ADMIN). Luego:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Gamer","price":1200.00,"category":"Electrónica"}'
```

#### 4.4 Crear un Pedido (con token de usuario normal)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <TOKEN_USUARIO>" \
  -H "Content-Type: application/json" \
  -d '{"items":[{"productId":"<product-uuid>","quantity":1}],"paymentToken":"tok_simulado"}'
```

#### 4.5 Verificar Correos en MailHog

Abre [http://localhost:8025](http://localhost:8025) y revisa los correos de bienvenida, pago exitoso o fallido.

---

## 📚 Documentación de la API (Swagger)

Una vez levantado el Gateway, accede a:

- **Swagger UI centralizado:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI agregado:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## ⚡ Patrones y Buenas Prácticas

| Patrón / Práctica             | Implementación                                            |
|-------------------------------|-----------------------------------------------------------|
| **Event‑Driven Architecture** | Kafka para comunicación asíncrona entre servicios         |
| **SAGA (Coreografía)**        | Orden → Inventory (reserva) → Payment → Order → Inventory |
| **Circuit Breaker**           | Resilience4J en Feign clients (product, inventory)        |
| **Token Relay**               | Gateway propaga JWT a servicios internos                  |
| **Client Credentials**        | Comunicación entre servicios (order → inventory)          |
| **Desnormalización**          | Order guarda nombre producto, email usuario en el momento |
| **Idempotencia**              | IdempotencyKey basado en UUID del pedido                  |

---

## 🐳 Nota sobre Infraestructura

El archivo `docker-compose.yml`  levanta únicamente **Kafka y Zookeeper**.  
**MailHog** se levanta con un comando independiente `docker run`. Ambos son necesarios para el correcto funcionamiento
del sistema.

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Haz fork, crea una rama y envía un pull request con tus mejoras épicas.

---
