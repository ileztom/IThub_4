package com.library.loan.controller;

import com.library.loan.dto.LoanRequest;
import com.library.loan.model.Loan;
import com.library.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Учёт выдачи книг", description = "Выдача, возврат, просмотр активных и просроченных выдач.")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Выдать книгу читателю",
            description = "Создаёт запись о выдаче. Уменьшает availableCopies и публикует LOAN_CREATED в RabbitMQ.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Книга выдана",
                    content = @Content(schema = @Schema(implementation = Loan.class))),
            @ApiResponse(responseCode = "400", description = "Нет копий или книга уже на руках"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PostMapping
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @Operation(summary = "Вернуть книгу",
            description = "Отмечает RETURNED, увеличивает availableCopies, публикует LOAN_RETURNED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Книга возвращена",
                    content = @Content(schema = @Schema(implementation = Loan.class))),
            @ApiResponse(responseCode = "400", description = "Книга уже возвращена")
    })
    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(
            @Parameter(description = "MongoDB ObjectId выдачи") @PathVariable String id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @Operation(summary = "Получить выдачу по ID")
    @ApiResponse(responseCode = "200", description = "Выдача найдена",
            content = @Content(schema = @Schema(implementation = Loan.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(
            @Parameter(description = "MongoDB ObjectId выдачи") @PathVariable String id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(summary = "Выдачи пользователя",
            description = "Все выдачи (ACTIVE, RETURNED, OVERDUE) для userId.")
    @ApiResponse(responseCode = "200", description = "Список выдач",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Loan.class))))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getUserLoans(
            @Parameter(description = "MongoDB ObjectId пользователя") @PathVariable String userId) {
        return ResponseEntity.ok(loanService.getUserLoans(userId));
    }

    @Operation(summary = "Все активные выдачи", description = "Только ADMIN и LIBRARIAN.")
    @ApiResponse(responseCode = "200", description = "Активные выдачи",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Loan.class))))
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<Loan>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @Operation(summary = "Просроченные выдачи", description = "Только ADMIN и LIBRARIAN.")
    @ApiResponse(responseCode = "200", description = "Просроченные выдачи",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Loan.class))))
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }
}
