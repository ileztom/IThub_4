# 📌 README.md — API + Postman Collection

## 📖 Описание проекта
Этот проект представляет собой **Spring Boot REST API** с аутентификацией и CRUD-операциями над пользователями и продуктами.  
К проекту прилагается **Postman коллекция** с автоматизированными тестами.  

---

## 📂 Структура коллекции
Коллекция `My_API_Testing_Collection.json` включает следующие группы:

- **Authentication**
  - `Login` — получение `accessToken` и `refreshToken`
  - `Refresh Token` — обновление `accessToken` по `refreshToken`
  - `Logout` — завершение сессии
- **Users**
  - `Get All Users`
  - `Get User by ID`
  - `Create User`
  - `Update User`
  - `Delete User`
- **Products**
  - `Get All Products`
  - `Get Product by ID`
  - `Create Product`
  - `Update Product`
  - `Delete Product`

Каждый запрос сопровождается тестами в Postman, которые проверяют корректность кода ответа и сохраняют токены в окружении.  

---

## ⚙️ Инструкция по настройке

1. Склонируй или распакуй проект:
   ```bash
   git clone https://github.com/ileztom/IThub_4/tree/main/Integration_testing/spring-boot-h2-api
   cd spring-boot-h2-api
   ```

2. Собери и запусти приложение:
   ```bash
   mvn clean package
   java -jar target/spring-boot-h2-api-0.0.1-SNAPSHOT.jar
   ```

3. Запусти Postman и импортируй:
   - Коллекцию `My_API_Testing_Collection.json`
   - Окружение `My_API_Environment.json`

4. Убедись, что сервер доступен по адресу:
   ```
   http://localhost:8080
   ```

---

## ▶️ Примеры запуска тестов

1. В Postman выбери коллекцию `My API Testing Collection`.
2. Нажми **Run Collection** (или `Ctrl+Shift+R`).
3. Запусти все тесты.
4. После успешного прохождения ты увидишь зелёные статусы:  

✅ `Login` возвращает `accessToken` и `refreshToken`  
✅ `Refresh Token` выдает новый `accessToken`  
✅ `CRUD Users` — создание, обновление, удаление пользователей работает  
✅ `CRUD Products` — тесты для продуктов выполняются успешно  

---

## 🖼 Примеры скриншотов успешного выполнения тестов

### ✅ Products
![Products](<img width="1920" height="1020" alt="Тесты_Products" src="https://github.com/user-attachments/assets/c41f3d6b-4747-4f08-874d-f83ff9c23c66" />
)

### ✅ Users
![Users](<img width="1920" height="1020" alt="Тесты_Users" src="https://github.com/user-attachments/assets/da10928b-1a26-4d53-9282-be81dac29d13" />)

---

## 📌 Примечания
- В проекте используется **H2 in-memory database**, данные очищаются при каждом запуске.
- Для демонстрации создан пользователь:
  ```
  email: admin@example.com
  password: admin123
  ```
- Все токены сохраняются в окружение Postman автоматически.  
