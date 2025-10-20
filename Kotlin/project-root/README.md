# 📦 Ktor JWT + PostgreSQL API

Полнофункциональное CRUD-приложение на **Ktor**, с хранением данных в **PostgreSQL**, поддержкой **JWT-аутентификации**, ролей пользователей и **WebSocket** для работы в реальном времени. Документация через **Swagger UI**, централизованная обработка ошибок и логирование запросов.

---

## 🚀 Возможности

✅ **CRUD** для сущностей:
- **/users** — пользователи (id, username, password, role)  
- **/posts** — посты (id, title, description, location, image, timestamp)  

✅ **Аутентификация и роли**:
- JWT-токены  
- Роли пользователей: `admin`, `user`  
- Ограничение доступа к эндпоинтам по ролям  

✅ **WebSocket**:
- `/chat` — чат для пользователей в реальном времени  
- `/announce` — сообщения от админа  

✅ Данные сохраняются в **PostgreSQL**  


---

## ⚙️ Установка и запуск

1. Склонируйте проект:  
```bash
git clone https://github.com/ileztom/IThub_4/tree/main/Kotlin/project-root
cd project-root
```
2. Соберите проект через Gradle:
```bash
./gradlew clean shadowJar
```
3. Запустите сервер с помощью Docker:
```bash
docker-compose up --build
```
4. Откройте в браузере:
```bash
http://localhost:8080/
```

## 📘 Документация API

Swagger UI автоматически генерируется при запуске сервера:

- Главная страница: ```http://localhost:8080/```
- OpenAPI JSON: ```http://localhost:8080/openapi.json```

## 🔍 Примеры запросов

### ▶ Регистрация пользователя
```bash
POST /auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "123456",
  "role": "user"
}
```

### ▶ Логин пользователя (JWT)
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "123456"
}
```
Ответ:
```bash
{
  "token": "<JWT-токен>"
}
```

### ▶ Добавить пост
```bash
POST /posts
Authorization: Bearer <JWT-токен>
Content-Type: application/json

{
  "title": "Мой первый пост",
  "description": "Пример поста",
  "location": "Москва",
  "image": "image_url"
}
```
