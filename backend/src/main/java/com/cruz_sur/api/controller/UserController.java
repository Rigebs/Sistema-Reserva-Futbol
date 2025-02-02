package com.cruz_sur.api.controller;


import com.cruz_sur.api.dto.UpdateClientAndSedeDto;
import com.cruz_sur.api.dto.UserDetailsDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.responses.TokenResponse;
import com.cruz_sur.api.service.imp.AuthenticationService;
import com.cruz_sur.api.service.imp.JwtService;
import com.cruz_sur.api.service.imp.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/users")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;



    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }


    @PutMapping("/updateClientOrSede")
    public ResponseEntity<TokenResponse> updateClientOrSede(@RequestBody UpdateClientAndSedeDto updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        String updatedToken = authenticationService.updateClientAndCompania(currentUser.getId(), updateRequest);
        return ResponseEntity.ok(new TokenResponse(updatedToken));
    }

    @PutMapping("/compania/{idCompania}/updateRoleCompania")
    public ResponseEntity<Map<String, String>> updateRoleToCompania(@PathVariable Long idCompania) {
        authenticationService.updateRoleToCompania(idCompania);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role updated successfully");
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/compania/{idCompania}/reject")
    public ResponseEntity<Map<String, String>> rejectCompania(@PathVariable Long idCompania) {
        authenticationService.rejectCompania(idCompania);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Compañía rechazada y asociaciones eliminadas exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> getUserById(@PathVariable Long id) {
        UserDetailsDTO userDetails = userService.byId(id);
        return ResponseEntity.ok(userDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TokenResponse> updateUser(@PathVariable Long id, @RequestBody UserDetailsDTO userDetailsDTO) {
        User updatedUser = userService.updateAllFields(id, userDetailsDTO);
        String newToken = jwtService.generateToken(updatedUser);
        return ResponseEntity.ok(new TokenResponse(newToken));
    }
}