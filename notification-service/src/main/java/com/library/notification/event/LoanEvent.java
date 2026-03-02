package com.library.notification.event;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoanEvent {
    private String eventType;
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
