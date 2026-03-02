package com.library.loan.service;

import com.library.loan.client.CatalogClient;
import com.library.loan.client.UserClient;
import com.library.loan.config.RabbitMQConfig;
import com.library.loan.dto.BookDto;
import com.library.loan.dto.LoanRequest;
import com.library.loan.dto.UserDto;
import com.library.loan.event.LoanEvent;
import com.library.loan.model.Loan;
import com.library.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CatalogClient catalogClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;

    public Loan createLoan(LoanRequest req) {
        if (loanRepository.existsByUserIdAndBookIdAndStatus(req.getUserId(), req.getBookId(), Loan.LoanStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active loan for this book");
        }

        UserDto user = userClient.getUser(req.getUserId());
        if (!user.isActive()) {
            throw new IllegalStateException("User account is not active");
        }

        BookDto book = catalogClient.decrementCopies(req.getBookId());

        Loan loan = Loan.builder()
                .userId(req.getUserId())
                .bookId(req.getBookId())
                .bookIsbn(book.getIsbn())
                .bookTitle(book.getTitle())
                .userFullName(user.getFullName())
                .dueDate(LocalDate.now().plusDays(req.getLoanDays() != null ? req.getLoanDays() : 14))
                .status(Loan.LoanStatus.ACTIVE)
                .notes(req.getNotes())
                .build();

        loan = loanRepository.save(loan);
        log.info("Loan created: {} for user {} book {}", loan.getId(), req.getUserId(), req.getBookId());

        publishEvent(LoanEvent.builder()
                .eventType("LOAN_CREATED")
                .loanId(loan.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .bookIsbn(book.getIsbn())
                .dueDate(loan.getDueDate())
                .timestamp(LocalDateTime.now())
                .build());

        return loan;
    }

    public Loan returnBook(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalStateException("Book already returned");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);
        loan = loanRepository.save(loan);

        catalogClient.incrementCopies(loan.getBookId());

        UserDto user = userClient.getUser(loan.getUserId());
        publishEvent(LoanEvent.builder()
                .eventType("LOAN_RETURNED")
                .loanId(loan.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .bookId(loan.getBookId())
                .bookTitle(loan.getBookTitle())
                .bookIsbn(loan.getBookIsbn())
                .dueDate(loan.getDueDate())
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Book returned for loan: {}", loanId);
        return loan;
    }

    public List<Loan> getUserLoans(String userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(Loan.LoanStatus.ACTIVE);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }

    public Loan getLoanById(String id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + id));
    }

    /** Scheduled every day at 9:00 AM — mark overdue and fire events */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        for (Loan loan : overdueLoans) {
            if (loan.getStatus() != Loan.LoanStatus.OVERDUE) {
                loan.setStatus(Loan.LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
            try {
                UserDto user = userClient.getUser(loan.getUserId());
                publishEvent(LoanEvent.builder()
                        .eventType("LOAN_OVERDUE")
                        .loanId(loan.getId())
                        .userId(user.getId())
                        .userEmail(user.getEmail())
                        .userFullName(user.getFullName())
                        .bookId(loan.getBookId())
                        .bookTitle(loan.getBookTitle())
                        .bookIsbn(loan.getBookIsbn())
                        .dueDate(loan.getDueDate())
                        .timestamp(LocalDateTime.now())
                        .build());
            } catch (Exception e) {
                log.error("Failed to send overdue event for loan {}: {}", loan.getId(), e.getMessage());
            }
        }
        if (!overdueLoans.isEmpty()) {
            log.info("Processed {} overdue loans", overdueLoans.size());
        }
    }

    private void publishEvent(LoanEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.LOAN_EXCHANGE,
                    RabbitMQConfig.LOAN_ROUTING_KEY,
                    event
            );
            log.debug("Published event: {}", event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish loan event: {}", e.getMessage());
        }
    }
}
