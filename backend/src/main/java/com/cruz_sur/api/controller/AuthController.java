package com.cruz_sur.api.controller;

import com.cruz_sur.api.jwt.JwtInvalidTokenService;
import com.cruz_sur.api.jwt.JwtUtil;
import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.dto.UserFormSelectionDTO;
import com.cruz_sur.api.repository.RolRepository;
import com.cruz_sur.api.service.IUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final JwtInvalidTokenService jwtInvalidTokenService;
    private final RolRepository rolRepository;

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> googleLogin(OAuth2AuthenticationToken authentication, HttpServletResponse response) throws IOException {
        if (authentication == null) {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        Map<String, Object> responseBody = new HashMap<>();

        String email = authentication.getPrincipal().getAttribute("email");
        String id = authentication.getPrincipal().getAttribute("sub");
        String logeo = authentication.getPrincipal().getAttribute("name");
        String imagenUrl = authentication.getPrincipal().getAttribute("picture");

        Optional<Usuario> existingUser = usuarioService.findByEmail(email);
        Usuario usuario;

        if (existingUser.isEmpty()) {
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setGoogleId(id);
            usuario.setLogeo(logeo);
            usuario.setImagenUrl(imagenUrl);

            Rol rol = usuarioService.getDefaultUserRole();
            usuario.setRol(Set.of(rol));
            usuario = usuarioService.save(usuario);
        } else {
            usuario = existingUser.get();
        }

        String accessToken = jwtUtil.generateToken(usuario.getEmail());

        responseBody.put("accessToken", accessToken);
        responseBody.put("userId", usuario.getId());
        responseBody.put("usuario", usuario);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUserAfterLogin(@PathVariable Long id, @RequestBody UserFormSelectionDTO selection) {
        if (selection == null) {
            return ResponseEntity.badRequest().body("Datos de selección no pueden ser nulos.");
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        Rol rol;

        try {
            switch (selection.getFormType().toUpperCase()) {
                case "CLIENTE":
                    rol = usuarioService.getDefaultUserRole();
                    usuario.getRol().clear();
                    usuario.getRol().add(rol);
                    // Aquí se llamará al método que acabas de definir
                    usuario.setCliente(usuarioService.getClienteById(selection.getClienteId()));
                    usuario.setSede(null);
                    break;
                case "SEDE":
                    rol = rolRepository.findByNomtipo("ROLE_ADMIN")
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    usuario.getRol().clear();
                    usuario.getRol().add(rol);
                    usuario.setSede(usuarioService.getSedeById(selection.getSedeId()));
                    usuario.setCliente(null);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Tipo de formulario no válido.");
            }

            usuarioService.save(usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



    @DeleteMapping("/logout/{id}")
    public ResponseEntity<?> logoutUser(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtInvalidTokenService.invalidateToken(token);
        }

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok().body("Sesión cerrada exitosamente.");
    }

}
