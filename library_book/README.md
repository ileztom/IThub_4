# 📚 Library Management System — Microservices Architecture

Система управления библиотекой на основе микросервисной архитектуры.

---

## Таблица микросервисов

| Микросервис | Порт | Функции | Зависимости | БД |
|---|---|---|---|---|
| **API Gateway** | 8080 | Маршрутизация, JWT-валидация, CORS | Все сервисы | — |
| **User Service** | 8081 | Регистрация, авторизация (JWT), профили | — | MongoDB: `library_users` |
| **Catalog Service** | 8082 | CRUD книг, поиск, управление копиями | — | MongoDB: `library_catalog` |
| **Loan Service** | 8083 | Выдача/возврат книг, проверка просрочек | User Service, Catalog Service | MongoDB: `library_loans` |
| **Notification Service** | 8084 | Уведомления о выдаче/возврате/просрочке | RabbitMQ (consumer) | MongoDB: `library_notifications` |
| **Frontend** | 3000 | React + TypeScript UI | API Gateway | — |

---

## Схема взаимодействия

```
                        ┌─────────────────────────────────────────────┐
                        │              React Frontend :3000            │
                        └─────────────────────┬───────────────────────┘
                                              │ HTTP REST
                        ┌─────────────────────▼───────────────────────┐
                        │           API Gateway :8080                   │
                        │  (JWT validation, routing, CORS, rate limit)  │
                        └──────┬──────────┬──────────┬────────┬────────┘
                               │          │          │        │
              ┌────────────────▼─┐  ┌────▼───┐  ┌──▼──────┐ │
              │  User Service    │  │ Catalog │  │  Loan   │ │
              │     :8081        │  │ :8082   │  │  :8083  │ │
              │ JWT generation   │  │ Books   │  │ Checkout│ │
              │ User management  │  │ Search  │  │ Returns │ │
              └──────────────────┘  └─────────┘  └────┬────┘ │
                      │                   │            │      │
                      │  Feign (REST)     │  Feign     │      │
                      └───────────────────┘◄───────────┘      │
                                                               │
                                          RabbitMQ Events     │
                                    ┌──────────────────────────▼──────┐
                                    │      Notification Service :8084   │
                                    │   (RabbitMQ consumer + REST API)  │
                                    └────────────────────────────────────┘
                                                     │
                                    ┌────────────────┴──────┐
                                    │    RabbitMQ :5672     │
                                    │  library.loans.queue  │
                                    └───────────────────────┘

                    ┌─────────────────────────────────────────┐
                    │           MongoDB :27017                  │
                    │  library_users | library_catalog          │
                    │  library_loans | library_notifications    │
                    └──────────────────────────────────────────┘
```

### Протоколы взаимодействия

| Взаимодействие | Протокол | Описание |
|---|---|---|
| Frontend → API Gateway | HTTP REST + JWT | Все запросы через единую точку входа |
| API Gateway → Services | HTTP REST | Proxy с форвардингом заголовков X-User-Id, X-User-Role |
| Loan → Catalog | OpenFeign (REST) + Resilience4j | Декремент/инкремент доступных копий с Circuit Breaker |
| Loan → User | OpenFeign (REST) + Resilience4j | Проверка активности пользователя |
| Loan → Notification | **RabbitMQ** (async) | События LOAN_CREATED, LOAN_RETURNED, LOAN_OVERDUE |

---

## Технологический стек

**Backend:**
- Java 17 + Spring Boot 3.2
- Spring Data MongoDB
- Spring Security + JWT (JJWT 0.12)
- Spring Cloud Gateway (API Gateway)
- Spring Cloud OpenFeign (межсервисные вызовы)
- Resilience4j (Circuit Breaker)
- Spring AMQP / RabbitMQ (асинхронные события)

**Frontend:**
- React 18 + TypeScript
- React Router v6
- Axios

**Инфраструктура:**
- MongoDB 7.0 (NoSQL, отдельная БД на каждый сервис)
- RabbitMQ 3 (message broker)
- Docker + Docker Compose
- Nginx (reverse proxy для frontend)

---

## API Reference

### Auth (User Service) — через Gateway на /api/auth
```
POST /api/auth/register   — регистрация
POST /api/auth/login      — вход, возвращает JWT
```

### Users — через Gateway на /api/users
```
GET  /api/users/{id}      — профиль пользователя
GET  /api/users           — все пользователи [ADMIN/LIBRARIAN]
PUT  /api/users/{id}      — обновление профиля
DEL  /api/users/{id}      — деактивация [ADMIN]
```

### Books — через Gateway на /api/books
```
GET  /api/books           — все книги (публично)
GET  /api/books/{id}      — книга по ID (публично)
GET  /api/books/search?q= — поиск (публично)
GET  /api/books/available — доступные книги
POST /api/books           — добавить книгу [LIBRARIAN/ADMIN]
PUT  /api/books/{id}      — обновить книгу [LIBRARIAN/ADMIN]
DEL  /api/books/{id}      — удалить книгу [ADMIN]
```

### Loans — через Gateway на /api/loans
```
POST /api/loans              — взять книгу (checkout)
PUT  /api/loans/{id}/return  — вернуть книгу
GET  /api/loans/{id}         — информация о выдаче
GET  /api/loans/user/{userId}— выдачи пользователя
GET  /api/loans/active       — активные выдачи [LIBRARIAN/ADMIN]
GET  /api/loans/overdue      — просроченные [LIBRARIAN/ADMIN]
```

### Notifications — через Gateway на /api/notifications
```
GET  /api/notifications/user/{userId}        — уведомления пользователя
GET  /api/notifications/user/{userId}/unread — непрочитанные
GET  /api/notifications/user/{userId}/count  — счётчик непрочитанных
PUT  /api/notifications/{id}/read            — прочитать
PUT  /api/notifications/user/{userId}/read-all — прочитать все
```

---

## Безопасность

- **JWT (HS256)** — токены генерируются в User Service, валидируются в каждом сервисе
- **API Gateway** — первая линия защиты, проверяет JWT перед проксированием
- **RBAC** — роли USER / LIBRARIAN / ADMIN с `@PreAuthorize` на уровне методов
- **Внутренние эндпоинты** (`/internal/**`) — защищены от внешнего доступа через Gateway
- **CORS** — настроен на уровне Gateway
- **Пароли** — хешируются BCrypt

---

## Хранение данных (NoSQL — MongoDB)

Каждый микросервис имеет **свою независимую базу данных** (Database per Service):

| Сервис | База | Коллекции |
|---|---|---|
| User Service | `library_users` | `users` |
| Catalog Service | `library_catalog` | `books` |
| Loan Service | `library_loans` | `loans` |
| Notification Service | `library_notifications` | `notifications` |

MongoDB выбрана как:
- Гибкая схема (нет жёсткой структуры таблиц)
- Горизонтальное масштабирование (sharding)
- Хорошо подходит для документоориентированных данных

---

## Отказоустойчивость и мониторинг

| Механизм | Где используется |
|---|---|
| **Circuit Breaker** (Resilience4j) | Loan Service → Catalog/User |
| **Retry** (RabbitMQ) | Notification Service (3 попытки с backoff) |
| **GlobalExceptionHandler** | Каждый сервис — единый обработчик ошибок |
| **Spring Actuator** | `/actuator/health`, `/actuator/metrics` |
| **Structured logging** (SLF4J) | Все сервисы |
| **Healthcheck** | Docker Compose для MongoDB и RabbitMQ |

---

## План поэтапной миграции (Монолит → Микросервисы)

### Этап 1 — Подготовка 
- Анализ монолита, выделение bounded contexts
- Настройка CI/CD pipeline
- Поднятие инфраструктуры: MongoDB, RabbitMQ, Docker registry
- Написание тестов для существующего монолита (baseline)

### Этап 2 — Strangler Fig: User Service 
- Вынесение аутентификации и управления пользователями в отдельный сервис
- Монолит продолжает работать, новые запросы на auth → User Service
- Миграция данных пользователей в MongoDB
- **Zero downtime**: Feature flag для постепенного переключения

### Этап 3 — Catalog Service 
- Вынесение каталога книг
- Синхронизация данных монолит ↔ Catalog Service через dual-write
- Поисковые запросы переключить на новый сервис
- Тестирование и снятие dual-write

### Этап 4 — Loan Service 
- Наиболее сложный сервис (зависит от User + Catalog)
- Настройка OpenFeign клиентов
- Интеграция Circuit Breaker
- Параллельная работа монолита для проверки корректности

### Этап 5 — Notification Service 
- Вынесение нотификаций
- Настройка RabbitMQ exchange и очередей
- Loan Service публикует события → Notification Service потребляет

### Этап 6 — API Gateway 
- Настройка маршрутов в Spring Cloud Gateway
- JWT валидация на уровне Gateway
- CORS, rate limiting, logging

### Этап 7 — Frontend миграция 
- React + TypeScript приложение
- Все запросы через единый API Gateway
- Nginx для раздачи статики

### Этап 8 — Завершение 
- Отключение монолита
- Нагрузочное тестирование
- Настройка мониторинга (Prometheus + Grafana опционально)
- Документирование API (Swagger/OpenAPI)

**Общее время миграции: ~12 недель**
**Downtime при каждом переключении: < 1 минуты (blue-green deployment)**

---

## Запуск проекта

### Быстрый старт (Docker Compose)
```bash
# Клонировать репозиторий
git clone <url>
cd library_book

# Скопировать конфиг
cp .env.example .env

# Запустить все сервисы
docker-compose up -d

# Открыть приложение
open http://localhost:3000

# RabbitMQ Management UI
open http://localhost:15672  # admin / password

# MongoDB
mongosh "mongodb://admin:password@localhost:27017"
```

### Локальная разработка (без Docker)
```bash
# 1. Запустить инфраструктуру
docker-compose up -d mongodb rabbitmq

# 2. Запустить сервисы (каждый в отдельном терминале)
cd user-service && mvn spring-boot:run
cd catalog-service && mvn spring-boot:run
cd loan-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run

# 3. Запустить frontend
cd frontend && npm install && npm start
```

### Первые шаги
1. Открыть http://localhost:3000
2. Зарегистрироваться как читатель
3. Добавить книги через панель управления (нужна роль LIBRARIAN/ADMIN)
4. Взять книгу и проверить уведомления
