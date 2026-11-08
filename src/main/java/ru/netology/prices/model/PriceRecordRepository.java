package ru.netology.prices.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRecordRepository extends JpaRepository<PriceRecord, Long> {

    List<PriceRecord> findByProductIdOrderByRecordedAtAsc(Long productId);
}
