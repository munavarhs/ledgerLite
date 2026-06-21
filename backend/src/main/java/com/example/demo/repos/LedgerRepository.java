package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

// That's it. No implementation. Spring generates it at runtime.
public interface LedgerRepository extends JpaRepository<LedgerEntryEntity, String> {
}