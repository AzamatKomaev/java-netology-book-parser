package ru.netology.prices.service;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Job {

    public record View(String id, String status, int total, int succeeded, int failed, Instant startedAt,
            List<String> errors) {
    }

    private final String id;
    private final int total;
    private final Instant startedAt = Instant.now();
    private final AtomicInteger succeeded = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();
    private final Queue<String> errors = new ConcurrentLinkedQueue<>();

    public Job(String id, int total) {
        this.id = id;
        this.total = total;
    }

    public void success() {
        succeeded.incrementAndGet();
    }

    public void fail(String url, String reason) {
        errors.add(url + ": " + reason);
        failed.incrementAndGet();
    }

    public View view() {
        int s = succeeded.get();
        int f = failed.get();
        String status = s + f < total ? "RUNNING" : (f == 0 ? "DONE" : "DONE_WITH_ERRORS");
        return new View(id, status, total, s, f, startedAt, List.copyOf(errors));
    }
}
