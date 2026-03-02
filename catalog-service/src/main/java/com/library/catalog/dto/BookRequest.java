package com.library.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String publisher;

    @NotNull
    @Min(value = 1000, message = "Invalid year")
    private Integer year;

    private String genre;
    private String description;
    private String coverUrl;

    @Min(value = 1, message = "At least 1 copy required")
    private int totalCopies = 1;
}
