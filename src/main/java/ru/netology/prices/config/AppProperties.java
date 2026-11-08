package ru.netology.prices.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Pool pool, Fetch fetch, Refresh refresh, Seed seed) {

    public record Pool(int coreSize, int maxSize, int queueCapacity) {
    }

    public record Fetch(Duration timeout, int hostParallelism, long delayMs) {
    }

    public record Refresh(boolean enabled, Duration initialDelay, Duration interval) {
    }

    public record Seed(boolean enabled, List<String> urls) {
    }
}
