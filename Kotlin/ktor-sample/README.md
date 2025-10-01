# Ktor Demo API

Это REST API на Kotlin, построенный с использованием **Ktor**, реализующий CRUD-операции для сущности `User`, аутентификацию через JWT и безопасное хранение паролей с помощью BCrypt. Проект использует **PostgreSQL** в качестве базы данных с ORM **Exposed** для работы с данными и **kotlinx.serialization** для обработки JSON.

## Возможности

- **CRUD-операции**:
  - Создание пользователя (POST `/users`).
  - Получение всех пользователей или фильтрация по email (GET `/users?email=...`).
  - Получение пользователя по ID (GET `/users/{id}`).
  - Удаление пользователя по ID (DELETE `/users/{id}`).
- **Аутентификация**:
  - Вход пользователя по email и паролю с выдачей JWT-токена (POST `/login`).
  - Защищённые маршруты (GET/DELETE) требуют JWT в заголовке `Authorization: Bearer <token>`.
- **Безопасность**:
  - Хэширование паролей с использованием BCrypt.
  - Авторизация на основе JWT с секретным ключом.
- **Обработка ошибок**:
  - Корректные HTTP-коды (200, 201, 400, 401, 404, 500).
  - Ответы с ошибками в формате JSON для некорректных запросов или проблем на сервере.
- **База данных**:
  - PostgreSQL с использованием Exposed ORM.
  - Таблица `users` с полями: `id`, `name`, `email` (уникальный), `password_hash`.

## Требования

- **JDK 17+**: Убедитесь, что установлен Java Development Kit.
- **Gradle**: Проект использует Gradle для автоматизации сборки.
- **PostgreSQL**: Запущенный сервер PostgreSQL (по умолчанию: localhost:5432).
- **Postman**: Для тестирования API (опционально).

## Установка

### 1. Клонирование репозитория
```bash
git clone <URL-репозитория>
cd <директория-репозитория>
```

### 2. Настройка PostgreSQL
1. Установите PostgreSQL, если он не установлен.
2. Создайте базу данных `ktor_demo`:
   ```bash
   psql -U postgres -c "CREATE DATABASE ktor_demo;"
   ```
3. Проверьте параметры подключения в `src/main/kotlin/Application.kt`:
   ```kotlin
   Database.connect(
       url = "jdbc:postgresql://localhost:5432/ktor_demo",
       driver = "org.postgresql.Driver",
       user = "postgres",
       password = "password"
   )
   ```
   Обновите `user` и `password`, если ваши настройки PostgreSQL отличаются.

### 3. Сборка проекта
```bash
./gradlew clean build
```

### 4. Запуск сервера
```bash
./gradlew run
```
Сервер запустится на `http://localhost:8080`.

## Эндпоинты API

| Метод  | Эндпоинт               | Описание                             | Аутентификация | Тело запроса / Параметры запроса                    |
|--------|------------------------|--------------------------------------|----------------|----------------------------------------------------|
| POST   | `/users`              | Создание нового пользователя          | Нет            | JSON: `{"name": "string", "email": "string", "password": "string"}` |
| POST   | `/login`              | Аутентификация и получение JWT       | Нет            | JSON: `{"email": "string", "password": "string"}`   |
| GET    | `/users`              | Получение всех пользователей (фильтр по email) | JWT | Query: `email=string` (опционально)                 |
| GET    | `/users/{id}`         | Получение пользователя по ID          | JWT            | Path: `id` (целое число)                           |
| DELETE | `/users/{id}`         | Удаление пользователя по ID           | JWT            | Path: `id` (целое число)                           |

### Примеры запросов (с использованием curl)

1. **Создание пользователя**:
   ```bash
   curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"John","email":"john@example.com","password":"secret"}'
   ```
   Ответ (201 Created):
   ```json
   {"id":1,"name":"John","email":"john@example.com"}
   ```

2. **Вход**:
   ```bash
   curl -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"email":"john@example.com","password":"secret"}'
   ```
   Ответ (200 OK):
   ```json
   {"token":"eyJ..."}
   ```

3. **Получение всех пользователей** (с опциональным фильтром по email):
   ```bash
   curl -X GET "http://localhost:8080/users?email=john@example.com" -H "Authorization: Bearer <token>"
   ```
   Ответ (200 OK):
   ```json
   [{"id":1,"name":"John","email":"john@example.com"}]
   ```

4. **Получение пользователя по ID**:
   ```bash
   curl -X GET http://localhost:8080/users/1 -H "Authorization: Bearer <token>"
   ```
   Ответ (200 OK):
   ```json
   {"id":1,"name":"John","email":"john@example.com"}
   ```

5. **Удаление пользователя**:
   ```bash
   curl -X DELETE http://localhost:8080/users/1 -H "Authorization: Bearer <token>"
   ```
   Ответ (204 No Content).

### Ответы с ошибками
- **400 Bad Request**: Некорректный ввод (например, дубликат email, неверный ID).
  ```json
  {"error":"Email already exists"}
  ```
- **401 Unauthorized**: Неверный или отсутствующий JWT-токен.
  ```json
  {"error":"Invalid credentials"}
  ```
- **404 Not Found**: Пользователь не найден.
  ```json
  {"error":"User not found"}
  ```
- **500 Internal Server Error**: Непредвиденные ошибки сервера.
  ```json
  {"error":"..."}
  ```

## Тестирование с Postman
1. Импортируйте файл `KtorDemo.postman_collection.json` в Postman.
2. Выполняйте запросы в порядке: Create User → Login (сохраняет JWT-токен) → Get/Delete.
3. Переменная `jwt_token` автоматически устанавливается после Login и используется в защищённых маршрутах.

## Структура проекта
```
├── src/main/kotlin
│   ├── Application.kt          # Конфигурация сервера и точка входа
│   ├── models/User.kt         # Сущность пользователя, DTO и операции с БД
│   ├── routes/UserRoutes.kt   # Маршруты API для CRUD и логина
│   ├── utils/PasswordUtils.kt # Хэширование и проверка паролей с BCrypt
├── src/main/resources
│   ├── application.conf       # Конфигурация Ktor (порт, хост)
├── build.gradle.kts           # Конфигурация сборки Gradle
├── README.md                  # Этот файл
├── KtorDemo.postman_collection.json # Коллекция Postman для тестирования
```

## Зависимости
- **Ktor**: 2.3.7 (сервер, аутентификация, JWT, обработка контента)
- **Exposed**: 0.41.1 (ORM для PostgreSQL)
- **PostgreSQL**: 42.7.1 (JDBC-драйвер)
- **BCrypt**: 0.10.2 (хэширование паролей)
- **kotlinx.serialization**: 1.6.0 (сериализация JSON)
- **Logback**: 1.4.11 (логирование)
