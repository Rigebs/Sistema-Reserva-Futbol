package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Atencion;
import com.cruz_sur.api.service.IAtencionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atenciones")
@AllArgsConstructor
public class AtencionController {

    private final IAtencionService atencionService;

    @GetMapping
    public ResponseEntity<List<Atencion>> all() {
        List<Atencion> atenciones = atencionService.all();
        return new ResponseEntity<>(atenciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        Atencion atencion = atencionService.findById(id).orElse(null);
        return atencion != null
                ? new ResponseEntity<>(atencion, HttpStatus.OK)
                : new ResponseEntity<>("Atencion no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody Atencion atencion) {
        atencionService.save(atencion);
        return new ResponseEntity<>("Atencion creada con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Atencion atencion) {
        atencionService.update(id, atencion);
        return new ResponseEntity<>("Atencion actualizada con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        atencionService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activada" : "desactivada";
        return new ResponseEntity<>("Atencion " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
