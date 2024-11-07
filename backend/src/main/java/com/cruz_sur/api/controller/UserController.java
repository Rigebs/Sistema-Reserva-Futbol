package com.cruz_sur.api.controller;


import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.dto.UserDetailResponseDto;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.service.imp.AuthenticationService;
import com.cruz_sur.api.service.imp.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/me")
    public ResponseEntity<UserDetailResponseDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Sede sede = currentUser.getSede();
        Sucursal sucursal = sede.getSucursal();
        Compania compania = sucursal.getCompania();
        Empresa empresa = compania.getEmpresa();

        UserDetailResponseDto response = new UserDetailResponseDto(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.isEnabled(),
                sede != null ? sede.getNombre() : null,
                sucursal != null ? sucursal.getNombre() : null,
                compania != null ? compania.getNombre() : null,
                compania != null && compania.getImagen() != null ? compania.getImagen().getImageUrl() : null,
                empresa != null ? empresa.getRuc() : null,
                empresa != null ? empresa.getRazonSocial() : null,
                empresa != null && empresa.getDistrito() != null ? empresa.getDistrito().getNombre() : null,
                empresa != null && empresa.getDistrito().getProvincia() != null ? empresa.getDistrito().getProvincia().getNombre() : null,
                empresa != null && empresa.getDistrito().getProvincia().getDepartamento() != null ? empresa.getDistrito().getProvincia().getDepartamento().getNombre() : null
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }


    @PutMapping("/updateClientOrSede")
    public ResponseEntity<String> updateClientOrSede(@RequestBody UpdateClientAndSedeDto updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        authenticationService.updateClientAndSede(currentUser.getId(), updateRequest);
        return ResponseEntity.ok("User updated successfully.");
    }
}