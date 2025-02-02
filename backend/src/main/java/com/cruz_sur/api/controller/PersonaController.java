package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.responses.PersonaClienteResponse;
import com.cruz_sur.api.responses.ReniecSunatResponse;
import com.cruz_sur.api.service.IPersonaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/personas")
@AllArgsConstructor
public class PersonaController {

    private final IPersonaService personaService;
    private final ReniecSunatResponse reniecSunatResponse;

    @PostMapping("/consultar-dni")
    public ResponseEntity<Map<String, Object>> consultarDni(@RequestParam String dni) {
        Optional<Persona> existingPersona = personaService.byDni(dni);
        if (existingPersona.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "La persona con este DNI ya está registrada"));
        }
        Map<String, Object> personaData = reniecSunatResponse.getPerson(dni);
        if (!personaData.isEmpty()) {
            return ResponseEntity.ok(personaData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron datos para el DNI"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Persona>> all() {
        List<Persona> personas = personaService.all();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> byId(@PathVariable Long id) {
        Persona persona = personaService.byId(id).orElse(null);
        return persona != null
                ? new ResponseEntity<>(persona, HttpStatus.OK)
                : new ResponseEntity<>("Persona no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody Persona persona) {
        PersonaClienteResponse response = personaService.save(persona);
        return new ResponseEntity<>(Map.of(
                "message", "Persona creada con éxito",
                "personaId", response.getPersona().getId(),
                "clienteId", response.getCliente().getId()
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Persona persona) {
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
