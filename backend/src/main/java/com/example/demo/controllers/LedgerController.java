package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final LedgerRepository repository;

    private static final String CACHE_KEY = "ledger:entries";

    public LedgerController(StringRedisTemplate redis, LedgerRepository repository) {
        this.redis = redis;
        // Our own mapper, date-aware. We control serialization end to end.
        this.repository = repository;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.mapper.disable(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @GetMapping("/entries")
    public LedgerDtos.LedgerResponse getEntries() throws Exception {
        // 1. CHECK CACHE — look for the JSON string in Redis
        String cached = redis.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            // Cache HIT — deserialize the JSON we stored ourselves, to the exact type
            return mapper.readValue(cached, LedgerDtos.LedgerResponse.class);
        }

        // 2. Cache miss → query the DATABASE now (was hardcoded before)
        List<LedgerEntryEntity> rows = repository.findAll();

        // 3. Map entities → DTOs (keep the API response shape stable)
        List<LedgerDtos.LedgerEntry> entries = rows.stream()
            .map(e -> new LedgerDtos.LedgerEntry(
                e.getId(), e.getDate(), e.getDescription(),
                e.getAmount(), e.getBalance()))
            .toList();

        LedgerDtos.LedgerResponse response = new LedgerDtos.LedgerResponse(entries);

        // 4. Store in cache    
        redis.opsForValue().set(CACHE_KEY, mapper.writeValueAsString(response), Duration.ofSeconds(60));

        // 5. Return the response
        return response;
    }

    private LedgerDtos.LedgerResponse buildLedger() {
        List<LedgerDtos.LedgerEntry> entries = List.of(
            new LedgerDtos.LedgerEntry("1", LocalDate.of(2026, 1, 3),
                "Opening balance", new BigDecimal("1000.00"), new BigDecimal("1000.00")),
            new LedgerDtos.LedgerEntry("2", LocalDate.of(2026, 1, 5),
                "Grocery store", new BigDecimal("-84.32"), new BigDecimal("915.68")),
            new LedgerDtos.LedgerEntry("3", LocalDate.of(2026, 1, 8),
                "Paycheck deposit", new BigDecimal("2400.00"), new BigDecimal("3315.68")),
            new LedgerDtos.LedgerEntry("4", LocalDate.of(2026, 1, 12),
                "Electric bill", new BigDecimal("-142.07"), new BigDecimal("3173.61")),
            new LedgerDtos.LedgerEntry("5", LocalDate.of(2026, 1, 15),
                "Coffee shop", new BigDecimal("-6.50"), new BigDecimal("3167.11")),
            new LedgerDtos.LedgerEntry("6", LocalDate.of(2026, 1, 20),
                "Refund", new BigDecimal("29.99"), new BigDecimal("3197.10"))
        );
        return new LedgerDtos.LedgerResponse(entries);
    }
}