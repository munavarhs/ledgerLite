package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public AuthController(JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          SessionService sessionService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest request) {
        // 1. Look up the user by username
        Optional<UserEntity> userOpt = userRepository.findByUsername(request.username());

        // 2. If found AND the password matches the stored hash, issue a token
        if (userOpt.isPresent() &&
            passwordEncoder.matches(request.password(), userOpt.get().getPasswordHash())) {
            String token = jwtService.generateToken(request.username());
            String sessionId = sessionService.createSession(request.username());
            return ResponseEntity.ok(new AuthDtos.LoginResponse(token, sessionId));
        }

        // 3. Otherwise, reject — same response whether user missing or password wrong
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // Logout — revoke a session by ID
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String sessionId) {
        sessionService.revokeSession(sessionId);
        return ResponseEntity.ok("Logged out");
    }

    // Active session count — demonstrates concurrent sessions
    @GetMapping("/sessions/count")
    public ResponseEntity<?> sessionCount() {
        return ResponseEntity.ok(sessionService.activeSessionCount());
    }
}