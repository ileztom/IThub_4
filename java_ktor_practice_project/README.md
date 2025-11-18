# 📘 Отчёт по учебной практике
### *Разработка микросервисной системы на Java Spring Boot, Ktor, Kafka и Docker*

---

## 🎓 **Справочная информация**

**Учебное заведение:** IThub college Moscow 
**Кафедра:** ИТ

**ОТЧЁТ ПО УЧЕБНОЙ ПРАКТИКЕ**

**Тема:**  
*Разработка распределённой микросервисной системы для управления пользователями, курсами и их рейтингами с использованием Java Spring Boot, Ktor, Kafka, PostgreSQL и Docker.*

**Студент:** Томов Илья Максимович  
**Группа:** 4П1.22  
**Преподаватель:** Чернышёв Александр Юрьевич

**Москва, 2025**

---

# 🎯 1. Цель практики

Целью практики является получение практических навыков разработки микросервисной архитектуры, взаимодействующей через REST API и Kafka, а также освоение:

- Java Spring Boot
- Kotlin Ktor
- Kafka (producer + consumer)
- PostgreSQL
- Docker и Docker Compose
- JWT-аутентификации
- Тестирования API с помощью Postman
- Проектирования архитектурных схем

---

# 🏗️ 2. Архитектура системы

Проект состоит из следующих микросервисов:

### 🔹 **user-service (Spring Boot)**
- Регистрация пользователей
- JWT-аутентификация
- Авторизация

### 🔹 **course-service (Spring Boot)**
- Создание курсов
- Получение списка курсов
- Проверка авторизации

### 🔹 **rating-service (Spring Boot + Kafka Producer)**
- Приём рейтинга от пользователя
- Сохранение в базу
- Отправка RatingEvent в Kafka (топик: `ratings`)

### 🔹 **recommendation-service (Ktor + Kafka Consumer)**
- Получение событий рейтингов
- Сохранение в PostgreSQL
- Формирование рекомендаций

### 🔹 **Инфраструктура**
- Kafka + Zookeeper
- PostgreSQL
- Docker Compose

---

# 📊 UML / Диаграмма компонентов
                      +-------------------+
                      |   user-service    |
                      | Auth, Users, JWT  |
                      +---------+---------+
                                |
                                | Authenticated Requests
                                |
    +-----------+      +--------v--------+      +-----------------+
    | PostgreSQL|<---->|  course-service |      | rating-service  |
    +-----------+      | CRUD Courses    |      | Ratings + Kafka |
                       +--------+--------+      +--------+--------+
                                |                        |
                                | Kafka Event (Rating)   |   
                                |                        |
                           +----v------------------------v----+
                           |     recommendation-service       |
                           |       Ktor + Kafka Consumer      |
                           +----------------+-----------------+
                                            |
                                     Recommendations API

# 📡 3. API-документация и примеры запросов

## 🔹 **User-service**
### POST `/api/auth/register`
```json
{
  "username": "ilya",
  "password": "1234"
}
```
### POST `/api/auth/login`
```json
{
  "username": "ilya",
  "password": "1234"
}
```
### Ответ
```json
{
  "token": "JWT_TOKEN"
}
```

## 🔹 **Course-service**
### POST `/api/courses`
##### Headers:
```json
{
  "Authorization": "Bearer <token>"
}
```
##### Body:
```json
{
  "title": "Java Basic",
  "description": "Основы языка Java"
}
```
### Ответ
```json
{
  "id": 1,
  "title": "Java Basic",
  "description": "Основы языка Java"
}
```

## 🔹 **Rating-service**
### POST `/api/ratings`
##### Headers:
```json
{
  "Authorization": "Bearer <token>"
}
```
##### Body:
```json
{
  "userId": 1,
  "courseId": 1,
  "score": 5
}
```

## 🔹 **Recommendation-service**
### GET `/api/recommendations/{userId}`
### Ответ
```json
{
    "courseId": 1,
    "score": 5
}
```

# 🖼️ 4. Скриншоты работы сервисов



# 🐳 5. Docker Compose и запуск проекта

## Запуск всей системы

### Для запуска всех микросервисов выполните:
```bash
  docker compose up --build
```

### Чтобы остановить:
```bash
    docker compose down
```

## 📌 Проверить состояние контейнеров
```bash
    docker compose ps
```

### Порты сервисов
| Сервис                 | Порт |
| ---------------------- | ---- |
| user-service           | 8081 |
| course-service         | 8082 |
| rating-service         | 8083 |
| recommendation-service | 8084 |
| Kafka                  | 9092 |
| PostgreSQL             | 5432 |

# 🧩 6. Выводы
### В рамках учебной практики была разработана микросервисная архитектура, которая включает:
- 4 полноценных микросервиса
- Взаимодействие между сервисами через Kafka
- Реализацию аутентификации и авторизации с JWT
- Использование PostgreSQL как основной базы данных
- Контейнеризацию всех сервисов в Docker Compose
- Event-driven взаимодействие между сервисами (события рейтингов)
### ект демонстрирует освоение студентом следующих областей:
- Разработка REST API
- Микросервисная архитектура
- Асинхронное взаимодействие сервисов
- Docker
- Основы бэкенд-разработки и интеграции компонентов
### Система успешно работает и может быть расширена, например:
- добавлением рекомендаций на основе машинного обучения
- расширением API
- интеграцией полноценного фронтенда

# 📎 7. Возможные улучшения
- Добавить кэширование (Redis)
- Реализовать балансировку нагрузки
- Создать OpenAPI (Swagger) спецификацию
- Добавить интеграционные тесты
- Ввести централизованный сбор логов (ELK Stack)
- Добавить мониторинг (Prometheus + Grafana)

