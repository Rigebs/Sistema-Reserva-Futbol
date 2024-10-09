package com.cruz_sur.api.controller;

import com.cruz_sur.api.jwt.JwtUtil;
import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.service.IUsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
