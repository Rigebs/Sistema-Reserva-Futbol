package com.cruz_sur.api.controller;


import com.cruz_sur.api.dto.PasswordResetDto;
import com.cruz_sur.api.dto.PasswordResetRequestDto;
import com.cruz_sur.api.service.imp.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class PasswordResetController {
    private final AuthenticationService authenticationService;



    // Endpoint para solicitar el restablecimiento de contraseña
    @PostMapping("/password/reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        try {
            authenticationService.requestPasswordReset(passwordResetRequestDto.getEmail());
            return ResponseEntity.ok("Password reset verification code sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para restablecer la contraseña
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        try {
            authenticationService.resetPassword(
                    passwordResetDto.getEmail(),
                    passwordResetDto.getVerificationCode(),
                    passwordResetDto.getNewPassword()
            );
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
