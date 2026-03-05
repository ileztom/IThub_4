package com.library.loan.dto;

import lombok.Data;

@Data
public class BookDto {
    private String id;
    private String isbn;
    private String title;
    private String author;
    private int availableCopies;
    private int totalCopies;
}
