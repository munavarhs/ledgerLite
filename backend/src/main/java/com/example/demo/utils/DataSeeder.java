package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(LedgerRepository ledgerRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.ledgerRepository = ledgerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed a user if none exists
        if (userRepository.count() == 0) {
            String hash = passwordEncoder.encode("password123");  // hash the raw password
            userRepository.save(new UserEntity("admin", hash));
            System.out.println("Seeded user 'admin'");
        }

        // Seed ledger (unchanged from 5A)
        if (ledgerRepository.count() == 0) {
            ledgerRepository.save(new LedgerEntryEntity("1", LocalDate.of(2026, 1, 3),
                "Opening balance", new BigDecimal("1000.00"), new BigDecimal("1000.00")));
            ledgerRepository.save(new LedgerEntryEntity("2", LocalDate.of(2026, 1, 5),
                "Grocery store", new BigDecimal("-84.32"), new BigDecimal("915.68")));
            ledgerRepository.save(new LedgerEntryEntity("3", LocalDate.of(2026, 1, 8),
                "Paycheck deposit", new BigDecimal("2400.00"), new BigDecimal("3315.68")));
            ledgerRepository.save(new LedgerEntryEntity("4", LocalDate.of(2026, 1, 12),
                "Electric bill", new BigDecimal("-142.07"), new BigDecimal("3173.61")));
            ledgerRepository.save(new LedgerEntryEntity("5", LocalDate.of(2026, 1, 15),
                "Coffee shop", new BigDecimal("-6.50"), new BigDecimal("3167.11")));
            ledgerRepository.save(new LedgerEntryEntity("6", LocalDate.of(2026, 1, 20),
                "Refund", new BigDecimal("29.99"), new BigDecimal("3197.10")));
            System.out.println("Seeded " + ledgerRepository.count() + " ledger entries");
        }
    }
}