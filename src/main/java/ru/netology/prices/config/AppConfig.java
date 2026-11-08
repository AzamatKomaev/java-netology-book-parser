package ru.netology.prices.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService parserExecutor(AppProperties props) {
        AtomicInteger counter = new AtomicInteger();
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "parser-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
        AppProperties.Pool p = props.pool();
        return new ThreadPoolExecutor(
                p.coreSize(), p.maxSize(), 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(p.queueCapacity()),
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36")
                .defaultHeader("Accept-Language", "ru,en;q=0.8")
                .codecs(c -> c.defaultCodecs().maxInMemorySize(8 * 1024 * 1024))
                .build();
    }
}
