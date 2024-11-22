package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.config.AuthException;
import com.cruz_sur.api.dto.LoginUserDto;
import com.cruz_sur.api.dto.RegisterUserDto;
import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.dto.VerifyUserDto;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.responses.LoginResponse;
import com.cruz_sur.api.responses.event.RoleUpdatedEvent;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final EmpresaRepository empresaRepository;

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

    public String handleGoogleLogin(OAuth2AuthenticationToken authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication token or principal is null");
        }
        OAuth2User oAuth2User = authentication.getPrincipal();
        System.out.println("Authenticated Google User: " + oAuth2User.getAttributes());
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Cambiar "\s+" a "\\s+" para que la expresión regular funcione correctamente
        String cleanedName = (name != null) ? name.replaceAll("\\s+", "") : null;

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(cleanedName); // Usar el nombre sin espacios
            newUser.setPassword(passwordEncoder.encode(authenticatedUsername));
            newUser.setEnabled(true);
            newUser.setUsuarioCreacion(authenticatedUsername);
            newUser.setFechaCreacion(LocalDateTime.now());

            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });
            newUser.setRoles(List.of(role));
            return userRepository.save(newUser);
        });

        return jwtService.generateToken(user);
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

    public String updateRoleToCompania(Long companiaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated admin user not found"));

        // Ensure the authenticated user has ROLE_ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        if (!adminUser.getRoles().contains(adminRole)) {
            throw new RuntimeException("User is not authorized to change the compania role.");
        }

        // Find and update the Compania entity
        Compania compania = companiaRepository.findById(companiaId)
                .orElseThrow(() -> new RuntimeException("Compania not found"));
        compania.setEstado('1');

        // Find the user associated with the company
        User user = (User) userRepository.findFirstBySede(compania)
                .orElseThrow(() -> new RuntimeException("No user associated with the selected compania."));
        user.setSede(compania);

        // Get all relevant roles
        List<Role> rolesList = roleRepository.findByNameIn(List.of("ROLE_COMPANIA", "ROLE_ESPERA", "ROLE_CLIENTE", "ROLE_ADMIN"));
        Map<String, Role> roles = rolesList.stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        // Update user roles
        user.getRoles().add(roles.get("ROLE_COMPANIA"));
        user.getRoles().remove(roles.get("ROLE_ESPERA"));
        user.getRoles().remove(roles.get("ROLE_CLIENTE"));
        user.getRoles().remove(roles.get("ROLE_ADMIN"));

        // Update user metadata
        user.setUsuarioModificacion(authentication.getName());
        user.setFechaModificacion(LocalDateTime.now());

        // Save user changes
        userRepository.save(user);

        // Generate a new token for the user
        String newToken = jwtService.generateToken(user);

        // Send confirmation email
        sendCompaniaApprovedEmail(user, compania, newToken);

        // Emit event for role update
        eventPublisher.publishEvent(new RoleUpdatedEvent(user));

        return newToken;
    }

    public void rejectCompania(Long companiaId) {
        Compania compania = companiaRepository.findById(companiaId)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));
        User user = (User) userRepository.findFirstBySede(compania)
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario asociado con esta compañía"));
        user.setSede(null);
        sendCompaniaRejectedEmail(user, compania);

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("El rol ROLE_USER no fue encontrado"));
        user.getRoles().clear();
        user.getRoles().add(roleUser);
        userRepository.save(user);
        Empresa empresa = compania.getEmpresa();
        if (empresa != null) {
            Cliente cliente = clienteRepository.findByEmpresa(empresa)
                    .orElseThrow(() -> new RuntimeException("Cliente asociado a la empresa no encontrado"));
            cliente.setEmpresa(null);
            cliente.setUsuarioModificacion("SYSTEM");
            cliente.setFechaModificacion(LocalDateTime.now());
            clienteRepository.save(cliente);
        }
        companiaRepository.delete(compania);

        if (empresa != null) {
            empresaRepository.deleteById(empresa.getId());
        }
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

    private void sendCompaniaApprovedEmail(User user, Compania compania, String token) {
        String subject = "Tu compañía ha sido admitida en Zemply";
        String imageUrl = "http://res.cloudinary.com/dpfcpo5me/image/upload/v1729427606/jv8mvgjlwnmzfwiuzyay.jpg";
        String zemplyUrl = "https://zemply.vercel.app/confirm?token=" + token;

        String htmlMessage = "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='text-align: center;'>"
                + "<h1 style='color: #4CAF50;'>Felicidades, " + compania.getNombre() + "!</h1>"
                + "<img src='" + imageUrl + "' alt='Zemply Logo' style='width: 80%; max-width: 400px; height: auto; border-radius: 8px;'>"
                + "<h2 style='margin-top: 20px;'>Tu compañía ha sido admitida</h2>"
                + "<p>Nos complace informarte que la compañía <strong>" + compania.getNombre() + "</strong> ha sido aprobada para participar en Zemply.</p>"
                + "<p>A partir de ahora, puedes comenzar a subir tus campos deportivos y gestionar tus reservas fácilmente.</p>"
                + "<a href='" + zemplyUrl + "' style='display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>"
                + "Confirmar y Comenzar en Zemply"
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

    private void sendCompaniaRejectedEmail(User user, Compania compania) {
        String subject = "Lamentamos informarte que tu compañía no ha sido admitida en Zemply";
        String imageUrl = "http://res.cloudinary.com/dpfcpo5me/image/upload/v1729427606/jv8mvgjlwnmzfwiuzyay.jpg";
        String zemplyUrl = "https://zemply.vercel.app/faq";

        String htmlMessage = "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='text-align: center;'>"
                + "<h1 style='color: #FF5252;'>Lo sentimos, " + compania.getNombre() + "</h1>"
                + "<img src='" + imageUrl + "' alt='Zemply Logo' style='width: 80%; max-width: 400px; height: auto; border-radius: 8px;'>"
                + "<h2 style='margin-top: 20px;'>Tu compañía no ha sido admitida</h2>"
                + "<p>Tras una revisión exhaustiva, lamentamos informarte que la compañía <strong>" + compania.getNombre()
                + "</strong> no cumple con los requisitos necesarios para formar parte de Zemply en este momento.</p>"
                + "<p>Te animamos a revisar los requisitos en nuestra <a href='" + zemplyUrl + "' style='color: #FF5252; text-decoration: none;'>sección de preguntas frecuentes</a> y realizar las mejoras necesarias.</p>"
                + "<p>Estamos disponibles para responder cualquier duda o consulta que tengas sobre este proceso.</p>"
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
