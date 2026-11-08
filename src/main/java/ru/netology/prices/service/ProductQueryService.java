package ru.netology.prices.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import ru.netology.prices.model.Product;
import ru.netology.prices.model.ProductRepository;

@Service
public class ProductQueryService {

    public record Page(List<Product> items, long total, int page, int size) {
    }

    private final ProductRepository repository;

    public ProductQueryService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page search(String q, BigDecimal minPrice, BigDecimal maxPrice, String sort, boolean desc, int page,
            int size) {
        String needle = q == null ? null : q.toLowerCase(Locale.ROOT);
        List<Product> filtered = repository.findAll().parallelStream()
                .filter(p -> needle == null || (p.getTitle() != null && p.getTitle().toLowerCase(Locale.ROOT).contains(needle)))
                .filter(p -> minPrice == null || (p.getPrice() != null && p.getPrice().compareTo(minPrice) >= 0))
                .filter(p -> maxPrice == null || (p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0))
                .sorted(comparator(sort, desc))
                .toList();

        int from = (int) Math.min((long) page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        return new Page(filtered.subList(from, to), filtered.size(), page, size);
    }

    private Comparator<Product> comparator(String sort, boolean desc) {
        Comparator<Product> c = switch (sort == null ? "id" : sort) {
            case "title" -> Comparator.comparing(Product::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "price" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
            case "parsedAt" -> Comparator.comparing(Product::getParsedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "source" -> Comparator.comparing(Product::getSource, Comparator.nullsLast(Comparator.naturalOrder()));
            case "id" -> Comparator.comparing(Product::getId);
            default -> throw new IllegalArgumentException("Unknown sort field: " + sort);
        };
        return desc ? c.reversed() : c;
    }
}
