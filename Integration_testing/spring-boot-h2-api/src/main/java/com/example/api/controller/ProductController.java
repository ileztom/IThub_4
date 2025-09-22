package com.example.api.controller;

import com.example.api.model.Product;
import com.example.api.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    public ProductController(ProductRepository productRepository){this.productRepository = productRepository;}

    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) String category){
        if(category==null) return productRepository.findAll();
        return productRepository.findByCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(
                        Map.of("error", "Product not found")
                ));
    }

    @PostMapping
    public Product create(@RequestBody Product p){
        return productRepository.save(p);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody Product np){
        return productRepository.findById(id).map(p -> {
            p.setName(np.getName());
            p.setCategory(np.getCategory());
            p.setPrice(np.getPrice());
            return productRepository.save(p);
        }).orElseGet(() -> (Product) java.util.Map.of("error","Product not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productRepository.findById(id).map(p -> {
            productRepository.delete(p);
            return ResponseEntity.ok(Map.of("status", "deleted"));
        }).orElseGet(() -> ResponseEntity.status(404).body(
                Map.of("error", "Product not found")
        ));
    }
}
