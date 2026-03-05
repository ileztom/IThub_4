package com.library.user.controller;

import com.library.user.dto.UpdateUserRequest;
import com.library.user.dto.UserResponse;
import com.library.user.service.UserService;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление профилями читателей")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить пользователя по ID (внутренний)",
            description = "Используется другими микросервисами без JWT.")
    @ApiResponse(responseCode = "200", description = "Пользователь найден",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getUserInternal(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Список всех пользователей", description = "Доступно только ADMIN и LIBRARIAN.")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Обновить профиль пользователя",
            description = "Обновляет ФИО, телефон и адрес.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль обновлён",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "Деактивировать пользователя", description = "Только ADMIN. Мягкое удаление — не физическое.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь деактивирован"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
