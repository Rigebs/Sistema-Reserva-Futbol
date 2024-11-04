package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campos")
@AllArgsConstructor
public class CampoController {

    private final ICampoService campoService;

    @GetMapping
    public ResponseEntity<List<Campo>> all() {
        List<Campo> campos = campoService.all();
        return new ResponseEntity<>(campos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> byId(@PathVariable Long id) {
        Campo campo = campoService.byId(id).orElse(null);
        return campo != null
                ? new ResponseEntity<>(campo, HttpStatus.OK)
                : new ResponseEntity<>("Campo no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody Campo campo) {
        campoService.save(campo);
        return new ResponseEntity<>("Campo creado con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Campo campo) {
        campoService.update(id, campo);
        return new ResponseEntity<>("Campo actualizado con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        campoService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Campo " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
