package com.library.notification.controller;

import com.library.notification.model.Notification;
import com.library.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "Уведомления создаются автоматически при получении событий из RabbitMQ: LOAN_CREATED, LOAN_RETURNED, LOAN_OVERDUE.")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Все уведомления пользователя",
            description = "Уведомления в порядке убывания даты создания.")
    @ApiResponse(responseCode = "200", description = "Список уведомлений",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @Operation(summary = "Непрочитанные уведомления")
    @ApiResponse(responseCode = "200", description = "Непрочитанные",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @Operation(summary = "Счётчик непрочитанных",
            description = "Используется фронтендом для бейджа на иконке уведомлений.")
    @ApiResponse(responseCode = "200", description = "Количество непрочитанных",
            content = @Content(schema = @Schema(example = "{\"count\": 3}")))
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnread(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(userId)));
    }

    @Operation(summary = "Отметить уведомление прочитанным")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Уведомление прочитано",
                    content = @Content(schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "400", description = "Уведомление не найдено")
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @Parameter(description = "MongoDB ObjectId уведомления") @PathVariable String id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "Прочитать все уведомления пользователя")
    @ApiResponse(responseCode = "204", description = "Все уведомления прочитаны")
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Все уведомления системы", description = "Только ADMIN и LIBRARIAN.")
    @ApiResponse(responseCode = "200", description = "Все уведомления",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}
