package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.User;
import com.cruz_sur.api.responses.event.RoleUpdatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleUpdateListener {
    private final JwtService jwtService;

    @EventListener
    public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {
        User updatedUser = event.getUser();
        String token = jwtService.generateToken(updatedUser);
        System.out.println("Nuevo token generado: " + token);
    }

}
