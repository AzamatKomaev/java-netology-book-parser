package ru.netology.prices.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.netology.prices.model.PriceRecord;
import ru.netology.prices.model.PriceRecordRepository;
import ru.netology.prices.model.Product;
import ru.netology.prices.model.ProductRepository;
import ru.netology.prices.parser.ParsedProduct;

@Service
public class ProductWriter {

    private final ProductRepository products;
    private final PriceRecordRepository history;

    public ProductWriter(ProductRepository products, PriceRecordRepository history) {
        this.products = products;
        this.history = history;
    }

    @Transactional
    public Product save(String url, String source, ParsedProduct parsed) {
        Product p = products.findByUrl(url).orElseGet(Product::new);
        Instant now = Instant.now();
        p.setUrl(url);
        p.setSource(source);
        p.setTitle(parsed.title());
        p.setDescription(parsed.description());
        p.setPrice(parsed.price());
        p.setCurrency(parsed.currency());
        p.setPublishedAt(parsed.publishedAt());
        p.setParsedAt(now);
        p = products.save(p);
        history.save(new PriceRecord(p.getId(), parsed.price(), now));
        return p;
    }
}
