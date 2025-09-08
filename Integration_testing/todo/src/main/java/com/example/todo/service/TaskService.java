package com.example.todo.service;

import com.example.todo.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        long id = counter.incrementAndGet();
        task.setId(id);
        if (task.getDescription() == null) {
            task.setDescription("");
        }
        tasks.put(id, task);
        return task;
    }

    public List<Task> getAllTasks(Boolean completed) {
        if (completed == null) {
            return new ArrayList<>(tasks.values());
        }
        return tasks.values().stream()
                .filter(t -> t.isCompleted() == completed)
                .collect(Collectors.toList());
    }

    public Task getTaskById(Long id) {
        return tasks.get(id);
    }

    public Task updateTask(Long id, Task updated) {
        Task existing = tasks.get(id);
        if (existing == null) return null;

        if (updated.getTitle() != null) {
            existing.setTitle(updated.getTitle());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        existing.setCompleted(updated.isCompleted());
        return existing;
    }

    public boolean deleteTask(Long id) {
        return tasks.remove(id) != null;
    }
}
