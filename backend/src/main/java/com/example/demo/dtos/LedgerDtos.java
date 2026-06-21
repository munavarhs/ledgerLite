package com.example.demo;   // ← match your actual package

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LedgerDtos {

    public record LedgerEntry(
            String id,
            LocalDate date,
            String description,
            BigDecimal amount,
            BigDecimal balance
    ) {}

    // NEW: wraps the list so the cached root is a concrete object, not a bare array
    public record LedgerResponse(List<LedgerEntry> entries) {}
}