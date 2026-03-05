package com.library.user.controller;

import com.library.user.dto.AuthResponse;
import com.library.user.dto.LoginRequest;
import com.library.user.dto.RegisterRequest;
import com.library.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация и вход в систему. JWT токен из ответа подставляйте в кнопку Authorize.")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Регистрация пользователя",
            description = "Создаёт аккаунт читателя и сразу возвращает JWT токен.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Логин/email заняты или ошибка валидации",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"Username already taken\"}") ))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @Operation(summary = "Вход в систему",
            description = "Принимает usernameOrEmail + password, возвращает JWT (TTL 24ч).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"Invalid username or password\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
