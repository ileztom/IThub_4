package com.library.loan.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEvent {
    private String eventType;  // LOAN_CREATED, LOAN_RETURNED, LOAN_OVERDUE
    private String loanId;
    private String userId;
    private String userEmail;
    private String userFullName;
    private String bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate dueDate;
    private LocalDateTime timestamp;
}
