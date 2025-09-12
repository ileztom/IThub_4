# ⚡ Ktor Posts API

Простой сервер на **Ktor (Kotlin)** для работы с постами.  
Поддерживает маршруты `GET`, `POST`, `DELETE`, работу с **query и path-параметрами**, JSON-сериализацию и тестирование через Postman.

---

## 🧪 Тестирование через Postman

1. Открой Postman.
2. Импортируй коллекцию:
   - Файл: postman/ktor-posts-collection.json
   - В Postman: Import → File → Upload Files → выбрать файл → Import
3. Коллекция появится в Collections → Ktor Posts API.
4. Запусти сервер и запусти коллекцию тестов в postman:
   - Запусти Application.kt -> main()
   - Запусти коллекцию Ktor Posts API в postman
   - Будет выполнена коллекция для проверки API постов на Ktor (GET, POST, DELETE, фильтрация, health-check)
       - Health-check
       - Create Post
       - Get All Posts
       - Get Post by ID
       - Get Posts by Tag / Title
       - Delete Post by ID

## ✅ Готово

Сервер запускается без ошибок, параметры обрабатываются корректно, возвращаются ответ с HTTP-статусом.

## 🔧 Дополнительно

Возможно по этой [ссылке](https://moscilez-7386465.postman.co/workspace/Ilya's-Workspace~0bc8566c-ea61-4376-8419-9b2fb53c3bb4/collection/48202788-05b3382e-b789-41d7-9764-e2fa5e27d289?action=share&creator=48202788) можно просмотреть результаты сборки коллекции.
