package com.library.loan.repository;

import com.library.loan.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByUserId(String userId);
    List<Loan> findByBookId(String bookId);
    List<Loan> findByStatus(Loan.LoanStatus status);
    Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.LoanStatus status);

    @Query("{ 'status': 'ACTIVE', 'dueDate': { '$lt': ?0 } }")
    List<Loan> findOverdueLoans(LocalDate now);

    List<Loan> findByUserIdAndStatus(String userId, Loan.LoanStatus status);
    boolean existsByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.LoanStatus status);
}
