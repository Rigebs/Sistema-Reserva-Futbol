package com.cruz_sur.api.controller;



import com.cruz_sur.api.config.AuthException;
import com.cruz_sur.api.dto.LoginUserDto;
import com.cruz_sur.api.dto.RegisterUserDto;
import com.cruz_sur.api.dto.VerifyUserDto;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.responses.LoginResponse;
import com.cruz_sur.api.service.imp.AuthenticationService;
import com.cruz_sur.api.service.imp.JwtService;
import com.cruz_sur.api.service.imp.TokenBlacklistService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(registeredUser);
        } catch (AuthException.UserAlreadyExistsException | AuthException.UsernameAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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