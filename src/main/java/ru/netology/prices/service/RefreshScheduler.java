package ru.netology.prices.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.netology.prices.model.Product;
import ru.netology.prices.model.ProductRepository;

@Component
@ConditionalOnProperty(name = "app.refresh.enabled", havingValue = "true")
public class RefreshScheduler {

    private static final Logger log = LoggerFactory.getLogger(RefreshScheduler.class);

    private final ProductRepository repository;
    private final ParseService parseService;

    public RefreshScheduler(ProductRepository repository, ParseService parseService) {
        this.repository = repository;
        this.parseService = parseService;
    }

    @Scheduled(initialDelayString = "${app.refresh.initial-delay}", fixedDelayString = "${app.refresh.interval}")
    public void refresh() {
        List<String> urls = repository.findAll().stream().map(Product::getUrl).toList();
        if (urls.isEmpty()) {
            return;
        }
        log.info("Scheduled refresh of {} products", urls.size());
        parseService.submit(urls);
    }
}
