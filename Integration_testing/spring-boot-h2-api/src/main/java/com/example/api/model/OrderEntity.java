package com.example.api.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Instant createdAt = Instant.now();

    @ElementCollection
    private java.util.List<Long> productIds;

    public OrderEntity(){}

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public Long getUserId(){return userId;}
    public void setUserId(Long userId){this.userId = userId;}
    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant createdAt){this.createdAt = createdAt;}
    public java.util.List<Long> getProductIds(){return productIds;}
    public void setProductIds(java.util.List<Long> productIds){this.productIds = productIds;}
}
