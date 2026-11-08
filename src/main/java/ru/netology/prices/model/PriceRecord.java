package ru.netology.prices.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PriceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(precision = 14, scale = 2)
    private BigDecimal price;

    private Instant recordedAt;

    public PriceRecord() {
    }

    public PriceRecord(Long productId, BigDecimal price, Instant recordedAt) {
        this.productId = productId;
        this.price = price;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public Instant getRecordedAt() { return recordedAt; }
}
