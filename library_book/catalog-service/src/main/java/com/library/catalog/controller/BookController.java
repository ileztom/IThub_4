package com.library.catalog.controller;

import com.library.catalog.dto.BookRequest;
import com.library.catalog.model.Book;
import com.library.catalog.service.BookService;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Каталог книг", description = "Управление книгами библиотеки. GET-запросы доступны без авторизации.")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Все книги каталога", description = "Возвращает список активных книг. Доступно без токена.")
    @ApiResponse(responseCode = "200", description = "Список книг",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Получить книгу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Книга найдена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Книга не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(
            @Parameter(description = "MongoDB ObjectId книги") @PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @Operation(summary = "Найти книгу по ISBN")
    @ApiResponse(responseCode = "200", description = "Книга найдена",
            content = @Content(schema = @Schema(implementation = Book.class)))
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(
            @Parameter(description = "ISBN книги", example = "978-5-17-090944-0") @PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    @Operation(summary = "Полнотекстовый поиск книг",
            description = "Ищет по названию, автору и ISBN. Без авторизации.")
    @ApiResponse(responseCode = "200", description = "Результаты поиска",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(
            @Parameter(description = "Поисковый запрос", example = "Толстой") @RequestParam String q) {
        return ResponseEntity.ok(bookService.searchBooks(q));
    }

    @Operation(summary = "Доступные книги", description = "Книги у которых availableCopies > 0.")
    @ApiResponse(responseCode = "200", description = "Доступные книги",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailable() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @Operation(summary = "Книги по жанру")
    @ApiResponse(responseCode = "200", description = "Книги жанра",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getByGenre(
            @Parameter(description = "Жанр", example = "Роман") @PathVariable String genre) {
        return ResponseEntity.ok(bookService.getBooksByGenre(genre));
    }

    @Operation(summary = "Добавить книгу в каталог", description = "Только LIBRARIAN и ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Книга добавлена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "ISBN уже существует или ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(request));
    }

    @Operation(summary = "Обновить книгу", description = "Только LIBRARIAN и ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Книга обновлена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "MongoDB ObjectId книги") @PathVariable String id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @Operation(summary = "Удалить (деактивировать) книгу", description = "Только ADMIN. Мягкое удаление.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Книга деактивирована"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "MongoDB ObjectId книги") @PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Internal] Уменьшить доступные копии",
            description = "Вызывается loan-service при выдаче книги. Не требует JWT.")
    @ApiResponse(responseCode = "200", description = "Копии обновлены",
            content = @Content(schema = @Schema(implementation = Book.class)))
    @PutMapping("/internal/{id}/decrement")
    public ResponseEntity<Book> decrementCopies(
            @Parameter(description = "MongoDB ObjectId книги") @PathVariable String id) {
        return ResponseEntity.ok(bookService.decrementAvailableCopies(id));
    }

    @Operation(summary = "[Internal] Увеличить доступные копии",
            description = "Вызывается loan-service при возврате книги. Не требует JWT.")
    @ApiResponse(responseCode = "200", description = "Копии обновлены",
            content = @Content(schema = @Schema(implementation = Book.class)))
    @PutMapping("/internal/{id}/increment")
    public ResponseEntity<Book> incrementCopies(
            @Parameter(description = "MongoDB ObjectId книги") @PathVariable String id) {
        return ResponseEntity.ok(bookService.incrementAvailableCopies(id));
    }
}
