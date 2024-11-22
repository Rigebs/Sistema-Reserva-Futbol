package com.cruz_sur.api.controller;

import com.cruz_sur.api.config.AuthException;
import com.cruz_sur.api.dto.LoginUserDto;
import com.cruz_sur.api.dto.RegisterUserDto;
import com.cruz_sur.api.dto.VerifyUserDto;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.responses.LoginResponse;
import com.cruz_sur.api.responses.TokenResponse;
import com.cruz_sur.api.responses.event.RoleUpdatedEvent;
import com.cruz_sur.api.service.imp.AuthenticationService;
import com.cruz_sur.api.service.imp.JwtService;
import com.cruz_sur.api.service.imp.TokenBlacklistService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final ApplicationEventPublisher eventPublisher;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            authenticationService.signup(registerUserDto);
            return ResponseEntity.ok("Registration successful! Please verify your email.");
        } catch (AuthException.UserAlreadyExistsException | AuthException.UsernameAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/oauth2/success")
    public void handleGoogleLogin(OAuth2AuthenticationToken authentication, HttpServletResponse response) throws IOException {
        try {
            // Generar el token usando el servicio
            String token = authenticationService.handleGoogleLogin(authentication);

            // Redirigir al frontend con el token como parámetro
            response.sendRedirect("http://localhost:4200/confir?token=" + token);
        } catch (IllegalArgumentException e) {
            // Manejar errores y redirigir con un mensaje de error
            response.sendRedirect("http://localhost:4200/login?error=" + e.getMessage());
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/update-token")
    public TokenResponse getUpdatedToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        eventPublisher.publishEvent(new RoleUpdatedEvent(user));
        String token = jwtService.generateToken(user);
        return new TokenResponse(token);
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String token = authorizationHeader.substring(7);

        tokenBlacklistService.blacklistToken(token);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logout successful");
    }


}