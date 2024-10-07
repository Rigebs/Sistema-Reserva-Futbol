package com.cruz_sur.api.controller;

import com.cruz_sur.api.jwt.JwtUtil;
import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.service.IUsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set; // Asegúrate de que esta línea esté presente

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> googleLogin(OAuth2AuthenticationToken authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication token is null"));
        }

        try {
            String email = authentication.getPrincipal().getAttribute("email");
            String id = authentication.getPrincipal().getAttribute("sub");
            String logeo = authentication.getPrincipal().getAttribute("name");
            String imagenUrl = authentication.getPrincipal().getAttribute("picture");

            // Busca el usuario existente
            Optional<Usuario> existingUser = usuarioService.findByEmail(email);
            Usuario usuario;

            if (existingUser.isEmpty()) {
                // Crea un nuevo usuario
                usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setGoogleId(id);
                usuario.setLogeo(logeo);
                usuario.setImagenUrl(imagenUrl);

                // Obtén el rol predeterminado
                Rol rol = usuarioService.getDefaultUserRole();
                usuario.setRol(Set.of(rol)); // Utiliza un Set para asignar el rol

                // Guarda el usuario
                try {
                    usuario = usuarioService.save(usuario);
                    System.out.println("Usuario creado: " + usuario);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "An error occurred while saving the user: " + e.getMessage()));
                }
            } else {
                usuario = existingUser.get();
            }

            String accessToken = jwtUtil.generateToken(usuario.getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            response.put("accessToken", accessToken);
            response.put("userId", usuario.getId());
            response.put("usuario", usuario);

            return ResponseEntity.ok().headers(headers).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing your request: " + e.getMessage()));
        }
    }

    // Método para saludar al usuario autenticado
    @GetMapping("/saludo")
    public ResponseEntity<String> saludarUsuario(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: No se ha autenticado.");
        }

        // Extraer el email y nombre del usuario autenticado
        String email = authentication.getPrincipal().getAttribute("email");
        String nombre = authentication.getPrincipal().getAttribute("name");

        String saludo = "¡Hola, " + nombre + "! Bienvenido de nuevo, tu email es: " + email;

        return ResponseEntity.ok(saludo);
    }
}
