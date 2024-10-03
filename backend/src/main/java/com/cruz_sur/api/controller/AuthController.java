package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.dto.LoginDto;
import com.cruz_sur.api.model.dto.UsuarioRegistrationDto;
import com.cruz_sur.api.security.JwtUtil;
import com.cruz_sur.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/testSave")
    public ResponseEntity<Map<String, String>> testSave(OAuth2AuthenticationToken authToken) {
        String email = authToken.getPrincipal().getAttribute("email");
        String id = authToken.getPrincipal().getAttribute("sub");
        String logeo = authToken.getPrincipal().getAttribute("name");
        String clave = "";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setGoogleId(id);
        usuario.setLogeo(logeo);
        usuario.setClave(clave);
        usuario.setRol(usuarioService.getDefaultUserRole());

        String refreshToken = jwtUtil.generateRefreshToken(email);
        usuario.setRefreshToken(refreshToken);

        Usuario savedUsuario = usuarioService.save(usuario);

        String accessToken = jwtUtil.generateToken(savedUsuario.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", savedUsuario.getRefreshToken());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUser(@RequestBody UsuarioRegistrationDto userDto) {
        String logeo = userDto.getLogeo();
        String clave = userDto.getClave();
        String email = userDto.getEmail();
        Long rolId = userDto.getRolId();
        Long clienteId = userDto.getClienteId();
        String usuarioCreacion = userDto.getUsuarioCreacion();

        Usuario newUser = usuarioService.createUser(logeo, clave, email, rolId, clienteId, usuarioCreacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Usuario usuario = usuarioService.authenticateUser(loginDto.getEmail(), loginDto.getClave());

        // Generar el token
        String accessToken = jwtUtil.generateToken(usuario.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }
}
