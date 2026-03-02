package com.library.loan.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "loans")
public class Loan {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String bookId;

    private String bookIsbn;
    private String bookTitle;
    private String userFullName;

    @CreatedDate
    private LocalDateTime loanDate;

    private LocalDate dueDate;
    private LocalDateTime returnDate;

    @Builder.Default
    private LoanStatus status = LoanStatus.ACTIVE;

    private String notes;

    public enum LoanStatus {
        ACTIVE, RETURNED, OVERDUE
    }

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate);
    }
}
