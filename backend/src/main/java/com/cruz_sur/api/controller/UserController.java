package com.cruz_sur.api.controller;


import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.service.imp.AuthenticationService;
import com.cruz_sur.api.service.imp.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
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
        authenticationService.updateClientAndSede(currentUser.getId(), updateRequest); // Obtener ID del usuario autenticado
        return ResponseEntity.ok("User updated successfully.");
    }
}