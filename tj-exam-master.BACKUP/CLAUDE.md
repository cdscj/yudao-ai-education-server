# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

天机学堂 (Tianji Academy) — a microservices-based online education platform built with Spring Cloud Alibaba. Multi-module Maven project, Java 11, Spring Boot 2.7.2, Spring Cloud 2021.0.3.

## Build & Run Commands

```bash
# Build the entire project (skip tests)
mvn clean install -DskipTests

# Build a single module with dependencies
mvn clean install -pl tj-course -am -DskipTests

# Run tests for a single module
mvn test -pl tj-course

# Run a single test class
mvn test -pl tj-course -Dtest=CourseControllerTest

# Run all tests
mvn test
```

## Architecture

### Infrastructure Stack
- **Registry & Config**: Nacos (service discovery + shared configuration, namespaces DEV_GROUP)
- **Gateway**: Spring Cloud Gateway on port 10010, routes with `StripPrefix=1` default filter
- **Inter-service calls**: OpenFeign (client interfaces in `tj-api`)
- **ORM**: MyBatis-Plus 3.4.3 (each service has its own database, e.g. `tj_course`, `tj_auth`)
- **Cache/Distributed locks**: Redis via Redisson
- **Message Queue**: RabbitMQ (via Spring AMQP)
- **Scheduled tasks**: XXL-Job
- **Distributed transactions**: Seata (configured via shared-config)
- **API docs**: Knife4j (Swagger) — per-service, accessed through gateway aggregation
- **Auth encryption**: JKS keystore (`tjxt.jks`)

### Module Map

| Module | Service Name | Purpose |
|--------|-------------|---------|
| `tj-common` | (library) | Shared auto-configurations (MVC, MyBatis, Redisson, MQ, Swagger, XXL-Job), base DTOs, exceptions, validators, utility classes |
| `tj-api` | (library) | Feign client interfaces + shared DTOs for inter-service calls; includes fallback configs |
| `tj-gateway` | `gateway-service` | API gateway — routing, auth filtering (`AccountAuthFilter`), request ID relay, CORS |
| `tj-auth` | `auth-service` | Authentication & RBAC authorization. Sub-modules: `tj-auth-common`, `tj-auth-gateway-sdk`, `tj-auth-resource-sdk`, `tj-auth-service` |
| `tj-user` | `user-service` | User profiles and management |
| `tj-course` | `course-service` | Course CRUD, catalogues, subjects, teacher assignments, drafts |
| `tj-exam` | `exam-service` | Question bank and exam management |
| `tj-learning` | `learning-service` | Learning progress, lesson records, interactions (Q&A), points leaderboard |
| `tj-media` | `media-service` | Media/asset handling |
| `tj-message` | `message-service` | SMS, notifications, notice templates. DDD sub-modules: `tj-message-api`, `tj-message-domain`, `tj-message-service` |
| `tj-pay` | `pay-service` | Payment channel integration (AliPay, etc.). DDD sub-modules: `tj-pay-api`, `tj-pay-domain`, `tj-pay-service` |
| `tj-trade` | `trade-service` | Orders, cart, refunds |
| `tj-promotion` | `promotion-service` | Coupons, exchange codes, promotions |
| `tj-search` | `search-service` | Search (Elasticsearch 7.12.1) |
| `tj-remark` | `remark-service` | Likes and review records |
| `tj-data` | `data-service` | Data center/analytics |
| `tj-xxljobDemo` | (demo) | XXL-Job example module |
| `tj-aopdemo` | (demo) | AOP demonstration module |

### Gateway Route Mapping
All routes use two-letter prefixes that get stripped before reaching the backend:

- `/as/**` → auth-service, `/us/**` → user-service, `/cs/**` → course-service
- `/es/**` → exam-service, `/ls/**` → learning-service, `/ms/**` → media-service
- `/sms/**` → message-service, `/ps/**` → pay-service, `/ts/**` → trade-service
- `/ss/**` → search-service, `/rs/**` → remark-service, `/ds/**` → data-service
- `/os/**` → order-service, `/prs/**` → promotion-service

### Layered Package Structure (per service)
Each service follows a consistent package layout under `com.tianji.{service}`:
- `controller/` — REST endpoints
- `service/` + `service/impl/` — business logic
- `mapper/` — MyBatis-Plus mappers
- `domain/dto/` — data transfer objects, `domain/po/` — persistence objects, `domain/vo/` — view objects, `domain/query/` — query parameter objects
- `config/` — service-specific Spring configuration
- `constants/` — service-level constants
- `handler/` — event handlers, `task/` — scheduled tasks

### `tj-common` Key Areas
- `autoconfigure/mvc/advice/` — global exception handler and response wrapper
- `autoconfigure/mvc/aspects/` — rate limiting, distributed lock aspects
- `autoconfigure/mybatis/` — MyBatis-Plus plugins (pagination, auto-fill)
- `autoconfigure/redisson/` — Redisson auto-config, `@Lock` annotation
- `autoconfigure/mq/` — RabbitMQ message converter and auto-config
- `exceptions/` — `CommonException`, `BizIllegalException`, `BadRequestException`, etc.
- `domain/dto/` — `BaseDTO` (with creater, updater, createTime, updateTime)
- `utils/` — `UserContext` (thread-local user info from gateway), `BeanUtils`, `JwtTool`, etc.

### Inter-service Communication Pattern
1. Feign client interface defined in `tj-api` (`com.tianji.api.client.*`)
2. Shared DTOs in `tj-api` (`com.tianji.api.dto.*`)
3. Fallback implementations registered via `FallbackConfig` in `tj-api`
4. Request ID is propagated via `RequestIdRelayConfiguration` (Feign interceptor)

### Configuration Model
- `bootstrap.yml` per service declares `spring.application.name`, Nacos discovery, and shared configs (`shared-spring.yaml`, `shared-redis.yaml`, `shared-mybatis.yaml`, `shared-logs.yaml`, `shared-feign.yaml`, `shared-mq.yaml`, `shared-seata.yaml`, `shared-xxljob.yaml`)
- Profile-specific overrides in `bootstrap-dev.yml` and `bootstrap-local.yml`
- Custom `tj.*` property namespace for swagger config, auth resource settings, and database name per service
