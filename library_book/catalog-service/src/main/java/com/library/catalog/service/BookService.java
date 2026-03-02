package com.library.catalog.service;

import com.library.catalog.dto.BookRequest;
import com.library.catalog.model.Book;
import com.library.catalog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(BookRequest req) {
        if (bookRepository.existsByIsbn(req.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN already exists: " + req.getIsbn());
        }
        Book book = Book.builder()
                .isbn(req.getIsbn())
                .title(req.getTitle())
                .author(req.getAuthor())
                .publisher(req.getPublisher())
                .year(req.getYear())
                .genre(req.getGenre())
                .description(req.getDescription())
                .coverUrl(req.getCoverUrl())
                .totalCopies(req.getTotalCopies())
                .availableCopies(req.getTotalCopies())
                .build();
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findByActiveTrue();
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.searchBooks(query);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    public Book updateBook(String id, BookRequest req) {
        Book book = getBookById(id);
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setPublisher(req.getPublisher());
        book.setYear(req.getYear());
        book.setGenre(req.getGenre());
        book.setDescription(req.getDescription());
        book.setCoverUrl(req.getCoverUrl());
        int diff = req.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(req.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        Book book = getBookById(id);
        book.setActive(false);
        bookRepository.save(book);
        log.info("Book deactivated: {}", id);
    }

    /** Called by loan-service to reserve/release copies */
    public Book decrementAvailableCopies(String id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for book: " + id);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookRepository.save(book);
    }

    public Book incrementAvailableCopies(String id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new IllegalStateException("All copies already available for book: " + id);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return bookRepository.save(book);
    }
}
