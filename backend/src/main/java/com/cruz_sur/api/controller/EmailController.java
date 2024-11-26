package com.cruz_sur.api.controller;

import com.cruz_sur.api.responses.EmailRequest;
import com.cruz_sur.api.service.imp.EmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/sendToSede")
    public ResponseEntity<String> sendEmailToUsersWithSedeId(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmailToUsersWithSedeId(emailRequest.getSubject(), emailRequest.getContent());
            return ResponseEntity.ok("Correo enviado a todos los usuarios con sedeId.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo.");
        }
    }

    // Endpoint para enviar correos a todos los usuarios con clienteId
    @PostMapping("/sendToCliente")
    public ResponseEntity<String> sendEmailToUsersWithClienteId(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmailToUsersWithClienteId(emailRequest.getSubject(), emailRequest.getContent());
            return ResponseEntity.ok("Correo enviado a todos los usuarios con clienteId.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo.");
        }
    }
}
