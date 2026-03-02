package com.library.loan.client;

import com.library.loan.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "catalog-service", url = "${catalog.service.url}")
public interface CatalogClient {

    @GetMapping("/api/books/{id}")
    BookDto getBook(@PathVariable String id);

    @PutMapping("/api/books/internal/{id}/decrement")
    BookDto decrementCopies(@PathVariable String id);

    @PutMapping("/api/books/internal/{id}/increment")
    BookDto incrementCopies(@PathVariable String id);
}
