package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Spring generates: SELECT * FROM app_user WHERE username = ?
    Optional<UserEntity> findByUsername(String username);
}