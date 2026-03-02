package com.library.catalog.repository;

import com.library.catalog.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    @Query("{ '$or': [ " +
           "  { 'title':  { '$regex': ?0, '$options': 'i' } }, " +
           "  { 'author': { '$regex': ?0, '$options': 'i' } }, " +
           "  { 'isbn':   { '$regex': ?0, '$options': 'i' } }  " +
           "] }")
    List<Book> searchBooks(String query);

    List<Book> findByGenreIgnoreCase(String genre);
    List<Book> findByAvailableCopiesGreaterThan(int copies);
    List<Book> findByActiveTrue();
}
