package ru.netology.prices.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.netology.prices.config.AppProperties;
import ru.netology.prices.parser.ParsedProduct;
import ru.netology.prices.parser.ProductSource;

@Service
public class ParseService {

    private static final Logger log = LoggerFactory.getLogger(ParseService.class);

    private final List<ProductSource> sources;
    private final ProductWriter writer;
    private final ExecutorService executor;
    private final AppProperties props;

    private final Map<String, Job> jobs = new ConcurrentHashMap<>();
    private final Set<String> inFlight = ConcurrentHashMap.newKeySet();
    private final Map<String, Semaphore> hostLimits = new ConcurrentHashMap<>();

    public ParseService(List<ProductSource> sources, ProductWriter writer, ExecutorService parserExecutor,
            AppProperties props) {
        this.sources = sources;
        this.writer = writer;
        this.executor = parserExecutor;
        this.props = props;
    }

    public Job.View submit(List<String> urls) {
        List<String> unique = urls.stream().map(String::trim).filter(s -> !s.isEmpty()).distinct().toList();
        Job job = new Job(UUID.randomUUID().toString(), unique.size());
        jobs.put(job.view().id(), job);
        unique.forEach(url -> CompletableFuture.runAsync(() -> process(url, job), executor));
        return job.view();
    }

    public Optional<Job.View> job(String id) {
        return Optional.ofNullable(jobs.get(id)).map(Job::view);
    }

    private void process(String url, Job job) {
        if (!inFlight.add(url)) {
            job.fail(url, "already being processed");
            return;
        }
        Semaphore limit = null;
        try {
            URI uri = URI.create(url);
            if (uri.getHost() == null || !uri.getScheme().startsWith("http")) {
                throw new IllegalArgumentException("not an http(s) url");
            }
            ProductSource source = sources.stream().filter(s -> s.supports(uri)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("no parser for host " + uri.getHost()));

            limit = hostLimits.computeIfAbsent(uri.getHost(), h -> new Semaphore(props.fetch().hostParallelism()));
            limit.acquire();
            ParsedProduct parsed;
            try {
                parsed = source.fetch(url);
                Thread.sleep(props.fetch().delayMs());
            } finally {
                limit.release();
            }
            writer.save(url, source.name(), parsed);
            job.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            job.fail(url, "interrupted");
        } catch (Exception e) {
            log.warn("Failed to parse {}: {}", url, e.toString());
            job.fail(url, String.valueOf(e.getMessage()));
        } finally {
            inFlight.remove(url);
        }
    }
}
