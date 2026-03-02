package com.library.catalog.controller;

import com.library.catalog.dto.BookRequest;
import com.library.catalog.model.Book;
import com.library.catalog.service.BookService;
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
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(@RequestParam String q) {
        return ResponseEntity.ok(bookService.searchBooks(q));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailable() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.getBooksByGenre(genre));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Book> updateBook(@PathVariable String id,
                                           @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /** Internal endpoints for loan-service */
    @PutMapping("/internal/{id}/decrement")
    public ResponseEntity<Book> decrementCopies(@PathVariable String id) {
        return ResponseEntity.ok(bookService.decrementAvailableCopies(id));
    }

    @PutMapping("/internal/{id}/increment")
    public ResponseEntity<Book> incrementCopies(@PathVariable String id) {
        return ResponseEntity.ok(bookService.incrementAvailableCopies(id));
    }
}
