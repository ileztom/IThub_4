package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 1. Создание задачи
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            Task created = taskService.createTask(task);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // 2. Получение списка задач (с фильтром completed)
    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) Boolean completed) {
        return taskService.getAllTasks(completed);
    }

    // 3. Получение конкретной задачи
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<>(new ErrorResponse("Task not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(task);
    }

    // 4. Обновление задачи
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updated) {
        Task task = taskService.updateTask(id, updated);
        if (task == null) {
            return new ResponseEntity<>(new ErrorResponse("Task not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(task);
    }

    // 5. Удаление задачи
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (!deleted) {
            return new ResponseEntity<>(new ErrorResponse("Task not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
    }

    // Вспомогательные классы для ошибок и сообщений
    static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) { this.error = error; }
    }

    static class MessageResponse {
        public String message;
        public MessageResponse(String message) { this.message = message; }
    }
}
