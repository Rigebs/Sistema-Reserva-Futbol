package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.dto.LoginDto;
import com.cruz_sur.api.model.dto.UsuarioRegistrationDto;
import com.cruz_sur.api.jwt.JwtUtil;
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
    public ResponseEntity<Map<String, Object>> testSave(OAuth2AuthenticationToken authToken) {
        Map<String, Object> response = new HashMap<>();

        if (authToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication token is null"));
        }

        String email = authToken.getPrincipal().getAttribute("email");
        String id = authToken.getPrincipal().getAttribute("sub");
        String logeo = authToken.getPrincipal().getAttribute("name");
        String imagenUrl = authToken.getPrincipal().getAttribute("picture"); // Extraer la URL de la imagen

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setGoogleId(id);
        usuario.setLogeo(logeo);
        usuario.setImagenUrl(imagenUrl);
        usuario.setClave(null);
        usuario.setRol(usuarioService.getDefaultUserRole());

        Usuario savedUsuario = usuarioService.save(usuario);
        String accessToken = jwtUtil.generateToken(savedUsuario.getEmail(), logeo);

        response.put("accessToken", accessToken);
        response.put("userId", savedUsuario.getId());
        response.put("usuario", savedUsuario);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUser(@RequestBody UsuarioRegistrationDto userDto) {
        String logeo = userDto.getLogeo();
        String clave = userDto.getClave();
        String email = userDto.getEmail();
        Long rolId = userDto.getRolId();
        Long clienteId = userDto.getClienteId();
        Long companiaId = userDto.getCompaniaId();
        String usuarioCreacion = userDto.getUsuarioCreacion();

        Usuario newUser = usuarioService.createUser(logeo, clave, email, rolId, clienteId, companiaId, usuarioCreacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/")
    public String home(){
        return "Hello!";
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Usuario usuario = usuarioService.authenticateUser(loginDto.getEmail(), loginDto.getClave());

        String accessToken = jwtUtil.generateToken(usuario.getEmail(), usuario.getLogeo());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Usuario> getUserDetails(@PathVariable Long userId) {
        try {
            Usuario usuario = usuarioService.findById(userId);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/userInfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(OAuth2AuthenticationToken authToken) {
        if (authToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication token is null"));
        }

        // Extraer los atributos del principal
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("attributes", authToken.getPrincipal().getAttributes());

        // También puedes incluir información del token de autenticación
        userInfo.put("name", authToken.getName());

        return ResponseEntity.ok(userInfo);
    }


}
