package com.example.demo;

// DTO = Data Transfer Object: the shape of data crossing the wire.
// Java 'record' = immutable data holder, like a TS type with a constructor.
public class AuthDtos {

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String token, String sessionId) {}
}