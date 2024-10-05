package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.dto.LoginDto;
import com.cruz_sur.api.model.dto.UsuarioRegistrationDto;
import com.cruz_sur.api.jwt.JwtUtil;
import com.cruz_sur.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUser(@RequestBody UsuarioRegistrationDto userDto) {
        String logeo = userDto.getLogeo();
        String clave = userDto.getClave();
        String email = userDto.getEmail();
        Long rolId = userDto.getRolId();
        Long clienteId = userDto.getClienteId();
        Long companiaId = userDto.getCompaniaId(); // Asegúrate de que esto esté en el DTO
        String usuarioCreacion = userDto.getUsuarioCreacion();

        Usuario newUser = usuarioService.createUser(logeo, clave, email, rolId, clienteId, companiaId, usuarioCreacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Usuario usuario = usuarioService.authenticateUser(loginDto.getEmail(), loginDto.getClave());

        // Generar el token
        String accessToken = jwtUtil.generateToken(usuario.getEmail(), usuario.getLogeo()); // Asegúrate de que logeo sea parte del usuario
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

}
