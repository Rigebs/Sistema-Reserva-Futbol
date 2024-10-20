package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.service.IPersonaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
@AllArgsConstructor
public class PersonaController {

    private final IPersonaService personaService;

    @GetMapping
    public ResponseEntity<List<Persona>> getAllPersonas() {
        List<Persona> personas = personaService.all();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPersonaById(@PathVariable Long id) {
        Persona persona = personaService.byId(id).orElse(null);
        return persona != null
                ? new ResponseEntity<>(persona, HttpStatus.OK)
                : new ResponseEntity<>("Persona no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createPersona(@RequestBody Persona persona) {
        personaService.save(persona);
        return new ResponseEntity<>("Persona creada con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePersona(@PathVariable Long id, @RequestBody Persona persona) {
        personaService.update(id, persona);
        return new ResponseEntity<>("Persona actualizada con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        personaService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activada" : "desactivada";
        return new ResponseEntity<>("Persona " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
