# 📦 Ktor JSON Storage API

Полнофункциональное CRUD-приложение на **Ktor**, которое хранит данные в **JSON-файлах**, имеет автоматическую документацию через **Swagger UI**, централизованную обработку ошибок и логирование запросов.

---

## 🚀 Возможности

✅ **CRUD** для трёх сущностей:
- **/posts** — посты (id, title, content)
- **/users** — пользователи (id, name, email, role)
- **/comments** — комментарии (id, postId, text)

✅ Данные сохраняются в **JSON-файлах**:
- **/data/posts.json** 
- **/data/users.json**
- **/data/comments.json**

✅ **Swagger UI** доступен на `/`  
✅ Централизованная обработка ошибок  
✅ Middleware (CallLogging)  
✅ Тестирование через Ktor Test Engine

---

## ⚙️ Установка и запуск

1. Склонируйте или скачайте проект:
   ```bash
   git clone https://github.com/ileztom/IThub_4/tree/main/Kotlin/ktor_json_storage
   cd ktor_json_storage  

2. Запустите сервер:
    ```bash
   ./gradlew run 
   
3. Откройте в браузере:
    ```bash
   http://localhost:8080/
   
## 📘 Документация API

Swagger UI автоматически генерируется при запуске сервера.

- Главная страница: http://localhost:8080/
- OpenAPI JSON: http://localhost:8080/openapi.json

## 🔍 Примеры запросов

### ▶ Добавить пользователя
    ```bash
    POST /users  
    Content-Type: application/json
    
    {
    "name": "Илья",
    "email": "ilya@example.com",
    "role": "admin"
    }

### ▶ Получить список пользователей
    ```bash
    GET /users  

### ▶ Добавить пост
    ```bash
    POST /posts  
    Content-Type: application/json
    
    {
    "title": "Мой первый пост",
    "content": "Ktor — это просто!"
    }

### ▶ Получить все посты
    ```bash
    GET /posts  

### ▶ Добавить комментарий
    ```bash
    POST /comments  
    Content-Type: application/json
    
    {
    "postId": 1,
    "text": "Отличный пост!"
    }

## 🧩 Структура проекта
```
ktor_json_storage/
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/
│   │   │   ├── Application.kt
│   │   │   ├── routes/
│   │   │   │   ├── PostRoutes.kt
│   │   │   │   ├── UserRoutes.kt
│   │   │   │   └── CommentRoutes.kt
│   │   │   └── service/
│   │   │       ├── PostService.kt
│   │   │       ├── UserService.kt
│   │   │       └── CommentService.kt
│   │   └── resources/
│   │       ├── application.conf
│   │       └── openapi.json
│   └── test/
│       └── kotlin/com/example/
│           └── ApplicationTest.kt
└── data/
    ├── posts.json
    ├── users.json
    └── comments.json
```

## 🧪 Тестирование

Запустите тесты:
   ```bash
   ./gradlew test
   ```
В src/test/kotlin/com/example/ApplicationTest.kt есть примеры API-тестов.

## ⚠️ Обработка ошибок

Все ошибки обрабатываются централизованно через StatusPages.
Примеры:
- **Невалидные запросы** → 400 Bad Request
- **Не найден ресурс**  → 404 Not Found
- **Внутренние ошибки**  → 500 Internal Server Error

