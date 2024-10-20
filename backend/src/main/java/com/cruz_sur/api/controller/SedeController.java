package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Sede;
import com.cruz_sur.api.service.ISedeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sedes")
@AllArgsConstructor
public class SedeController {

    private final ISedeService sedeService;

    @GetMapping
    public ResponseEntity<List<Sede>> getAllSedes() {
        List<Sede> sedes = sedeService.all();
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSedeById(@PathVariable Long id) {
        Sede sede = sedeService.byId(id).orElse(null);
        return sede != null
                ? new ResponseEntity<>(sede, HttpStatus.OK)
                : new ResponseEntity<>("Sede no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createSede(@RequestBody Sede sede) {
        sedeService.save(sede);
        return new ResponseEntity<>("Sede creada con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSede(@PathVariable Long id, @RequestBody Sede sede) {
        sedeService.update(id, sede);
        return new ResponseEntity<>("Sede actualizada con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        sedeService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activada" : "desactivada";
        return new ResponseEntity<>("Sede " + statusMessage + " con éxito", HttpStatus.OK);
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Sede>> findSedesBySucursalId(@PathVariable Long sucursalId) {
        List<Sede> sedes = sedeService.findSucursalById(sucursalId);
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }
}
