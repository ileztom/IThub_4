package com.library.loan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanRequest {
    @NotBlank(message = "Book ID is required")
    private String bookId;

    @NotBlank(message = "User ID is required")
    private String userId;

    private Integer loanDays = 14; // default 2 weeks
    private String notes;
}
