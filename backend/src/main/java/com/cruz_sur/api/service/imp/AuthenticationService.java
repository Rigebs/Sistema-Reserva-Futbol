package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.config.AuthException;
import com.cruz_sur.api.dto.LoginUserDto;
import com.cruz_sur.api.dto.RegisterUserDto;
import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.dto.VerifyUserDto;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.Role;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.ClienteRepository;
import com.cruz_sur.api.repository.CompaniaRepository;
import com.cruz_sur.api.repository.RoleRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.responses.event.RoleUpdatedEvent;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final CompaniaRepository companiaRepository;
    private final ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    public void signup(RegisterUserDto registerUserDto) {
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

    }

    public String updateClientAndCompania(Long userId, UpdateClientAndSedeDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCliente(null);
        user.setSede(null);
        user.getRoles().clear();
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (dto.getClienteId() != null && dto.getCompaniaId() != null) {
            throw new RuntimeException("User cannot have both cliente and compania.");
        }

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente not found."));
            user.setCliente(cliente);

            updateRolesForCliente(user);

            Role clienteRole = roleRepository.findByName("ROLE_CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_CLIENTE not found"));
            if (!user.getRoles().contains(clienteRole)) {
                user.getRoles().add(clienteRole);
            }

        }

        if (dto.getCompaniaId() != null) {
            Compania compania = companiaRepository.findById(dto.getCompaniaId())
                    .orElseThrow(() -> new RuntimeException("Compania not found."));
            user.setSede(compania);

            Role esperaRole = roleRepository.findByName("ROLE_ESPERA")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_ESPERA not found"));
            if (!user.getRoles().contains(esperaRole)) {
                user.getRoles().add(esperaRole);
            }

            updateRolesForCompania(user);
        }

        user.setUsuarioModificacion(authenticatedUsername);
        user.setFechaModificacion(LocalDateTime.now());

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    private void updateRolesForCliente(User user) {
        removeRole(user, "ROLE_USER");
        removeRole(user, "ROLE_ADMIN");
        removeRole(user, "ROLE_ESPERA");
    }

    private void updateRolesForCompania(User user) {
        removeRole(user, "ROLE_USER");
        removeRole(user, "ROLE_ADMIN");
        removeRole(user, "ROLE_CLIENTE");
    }

    private void removeRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
        user.getRoles().remove(role);
    }

    public void updateRoleToCompania(Long companiaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated admin user not found"));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        if (!adminUser.getRoles().contains(adminRole)) {
            throw new RuntimeException("User is not authorized to change the compania role.");
        }
        Compania compania = companiaRepository.findById(companiaId)
                .orElseThrow(() -> new RuntimeException("Compania not found"));
        compania.setEstado('1');

        User user = (User) userRepository.findFirstBySede(compania)
                .orElseThrow(() -> new RuntimeException("No user associated with the selected compania."));
        user.setSede(compania);

        List<Role> rolesList = roleRepository.findByNameIn(List.of("ROLE_COMPANIA", "ROLE_ESPERA", "ROLE_CLIENTE", "ROLE_ADMIN"));

        Map<String, Role> roles = rolesList.stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        user.getRoles().add(roles.get("ROLE_COMPANIA"));
        user.getRoles().remove(roles.get("ROLE_ESPERA"));
        user.getRoles().remove(roles.get("ROLE_CLIENTE"));
        user.getRoles().remove(roles.get("ROLE_ADMIN"));

        user.setUsuarioModificacion(authentication.getName());
        user.setFechaModificacion(LocalDateTime.now());
        userRepository.save(user);
        sendCompaniaApprovedEmail(user, compania);
        eventPublisher.publishEvent(new RoleUpdatedEvent(user));
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
            sendVerificationEmail(user);
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

    private void sendCompaniaApprovedEmail(User user, Compania compania) {
        String subject = "Tu compañía ha sido admitida en Zemply";
        String imageUrl = "http://res.cloudinary.com/dpfcpo5me/image/upload/v1729427606/jv8mvgjlwnmzfwiuzyay.jpg";
        String zemplyUrl = "https://zemply.vercel.app";

        String htmlMessage = "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='text-align: center;'>"
                + "<h1 style='color: #4CAF50;'>Felicidades, " + compania.getNombre() + "!</h1>"
                + "<img src='" + imageUrl + "' alt='Zemply Logo' style='width: 80%; max-width: 400px; height: auto; border-radius: 8px;'>"
                + "<h2 style='margin-top: 20px;'>Tu compañía ha sido admitida</h2>"
                + "<p>Nos complace informarte que la compañía <strong>" + compania.getNombre() + "</strong> ha sido aprobada para participar en Zemply.</p>"
                + "<p>A partir de ahora, puedes comenzar a subir tus campos deportivos y gestionar tus reservas facilmente.</p>"
                + "<a href='" + zemplyUrl + "' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>"
                + "Comenzar en Zemply"
                + "</a>"
                + "<p style='margin-top: 20px;'>Si tienes preguntas o necesitas ayuda, no dudes en contactarnos.</p>"
                + "<footer style='margin-top: 40px; font-size: 12px; color: #777;'>"
                + "<p>&copy; 2024 Zemply. Todos los derechos reservados.</p>"
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
