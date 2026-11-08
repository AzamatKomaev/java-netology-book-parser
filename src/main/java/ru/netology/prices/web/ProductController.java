package ru.netology.prices.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.netology.prices.model.PriceRecord;
import ru.netology.prices.model.PriceRecordRepository;
import ru.netology.prices.service.Job;
import ru.netology.prices.service.ParseService;
import ru.netology.prices.service.ProductQueryService;

@RestController
public class ProductController {

    private final ParseService parseService;
    private final ProductQueryService queryService;
    private final PriceRecordRepository history;

    public ProductController(ParseService parseService, ProductQueryService queryService,
            PriceRecordRepository history) {
        this.parseService = parseService;
        this.queryService = queryService;
        this.history = history;
    }

    @PostMapping("/api/parse")
    public ResponseEntity<Job.View> parse(@Valid @RequestBody ParseRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(parseService.submit(request.urls()));
    }

    @GetMapping("/api/jobs/{id}")
    public ResponseEntity<Job.View> job(@PathVariable String id) {
        return parseService.job(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/answer")
    public ProductQueryService.Page answer(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 200) {
            throw new IllegalArgumentException("page must be >= 0 and size must be 1..200");
        }
        return queryService.search(q, minPrice, maxPrice, sort, "desc".equalsIgnoreCase(dir), page, size);
    }

    @GetMapping("/api/products/{id}/history")
    public List<PriceRecord> history(@PathVariable Long id) {
        return history.findByProductIdOrderByRecordedAtAsc(id);
    }
}
