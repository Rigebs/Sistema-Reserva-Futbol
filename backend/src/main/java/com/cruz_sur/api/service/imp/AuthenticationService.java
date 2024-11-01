package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.config.AuthException;
import com.cruz_sur.api.dto.LoginUserDto;
import com.cruz_sur.api.dto.RegisterUserDto;
import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.dto.VerifyUserDto;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Role;
import com.cruz_sur.api.model.Sede;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.ClienteRepository;
import com.cruz_sur.api.repository.RoleRepository;
import com.cruz_sur.api.repository.SedeRepository;
import com.cruz_sur.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final SedeRepository sedeRepository;
    private final ClienteRepository clienteRepository;


    public User signup(RegisterUserDto registerUserDto) {
        if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            throw new AuthException.UserAlreadyExistsException("Email already registered");
        }

        if (userRepository.findByUsername(registerUserDto.getUsername()).isPresent()) {
            throw new AuthException.UsernameAlreadyExistsException("Username already taken");
        }

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User newUser = new User();
        newUser.setEmail(registerUserDto.getEmail());
        newUser.setUsername(registerUserDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        newUser.setEnabled(false);
        newUser.setUsuarioCreacion(authenticatedUsername);
        newUser.setFechaCreacion(LocalDateTime.now());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        newUser.setRoles(List.of(userRole));

        String verificationCode = generateVerificationCode();
        newUser.setVerificationCode(verificationCode);
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));

        userRepository.save(newUser);

        sendVerificationEmail(newUser);

        return newUser;
    }

    public void updateClientAndSede(Long userId, UpdateClientAndSedeDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setCliente(null);
        user.setSede(null);
        user.getRoles().clear();

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (dto.getClienteId() != null && dto.getSedeId() != null) {
            throw new IllegalArgumentException("User cannot have both cliente and sede.");
        }

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente not found."));
            user.setCliente(cliente);

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("User role not found"));
            user.getRoles().add(userRole);
        }

        if (dto.getSedeId() != null) {
            Sede sede = sedeRepository.findById(dto.getSedeId())
                    .orElseThrow(() -> new RuntimeException("Sede not found."));
            user.setSede(sede);

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            user.getRoles().add(adminRole);
        }

        user.setUsuarioModificacion(authenticatedUsername);
        user.setFechaModificacion(LocalDateTime.now());

        userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        User user;

        if (loginUserDto.getIdentifier().contains("@")) {
            user = userRepository.findByEmail(loginUserDto.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByUsername(loginUserDto.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        return user;
    }


    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void requestPasswordReset(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user); // Revisa si esta línea se ejecuta
            userRepository.save(user);
            System.out.println("Email de restablecimiento enviado a: " + email);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resetPassword(String email, String verificationCode, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(verificationCode)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Please verify your email";
        String imageUrl = "http://res.cloudinary.com/dpfcpo5me/image/upload/v1729427606/jv8mvgjlwnmzfwiuzyay.jpg";
        String htmlMessage = "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='text-align: center;'>"
                + "<h1 style='color: #4CAF50;'>Welcome to Reserva!</h1>"
                + "<img src='" + imageUrl + "' alt='Reserva Logo' style='width: 80%; max-width: 400px; height: auto; border-radius: 8px;'>"
                + "<h2 style='margin-top: 20px;'>Verification Code</h2>"
                + "<p>Use the following code to verify your email:</p>"
                + "<h3 style='font-size: 24px; font-weight: bold; color: #4CAF50;'>" + user.getVerificationCode() + "</h3>"
                + "<h4>Explore various sports activities and fields!</h4>"
                + "<p>If you did not create an account, please ignore this email.</p>"
                + "<footer style='margin-top: 40px; font-size: 12px; color: #777;'>"
                + "<p>&copy; 2024 Reserva. All rights reserved.</p>"
                + "</footer>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // Código de 6 dígitos
        return String.valueOf(code);
    }



}
