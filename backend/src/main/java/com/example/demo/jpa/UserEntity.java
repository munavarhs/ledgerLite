package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")   // "user" is a reserved word in many SQL dialects — avoid it
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // auto-increment ID
    private Long id;

    private String username;
    private String passwordHash;   // stores the BCrypt hash, NEVER the raw password

    protected UserEntity() {}

    public UserEntity(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}