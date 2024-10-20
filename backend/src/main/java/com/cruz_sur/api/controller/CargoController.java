package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Cargo;
import com.cruz_sur.api.service.ICargoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargos")
@AllArgsConstructor
public class CargoController {

    private final ICargoService cargoService;

    @GetMapping
    public ResponseEntity<List<Cargo>> getAllCargos() {
        List<Cargo> cargos = cargoService.all();
        return new ResponseEntity<>(cargos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCargoById(@PathVariable Long id) {
        Cargo cargo = cargoService.byId(id).orElse(null);
        return cargo != null
                ? new ResponseEntity<>(cargo, HttpStatus.OK)
                : new ResponseEntity<>("Cargo no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createCargo(@RequestBody Cargo cargo) {
        cargoService.save(cargo);
        return new ResponseEntity<>("Cargo creado con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCargo(@PathVariable Long id, @RequestBody Cargo cargo) {
        cargoService.update(id, cargo);
        return new ResponseEntity<>("Cargo actualizado con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        cargoService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Cargo " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
