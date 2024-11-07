package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Sucursal;
import com.cruz_sur.api.service.ISucursalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
@AllArgsConstructor
public class SucursalController {

    private final ISucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<Sucursal>> getAllSucursales() {
        List<Sucursal> sucursales = sucursalService.all();
        return new ResponseEntity<>(sucursales, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSucursalById(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.byId(id).orElse(null);
        return sucursal != null
                ? new ResponseEntity<>(sucursal, HttpStatus.OK)
                : new ResponseEntity<>("Sucursal no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Sucursal> save(@RequestBody Sucursal sucursal) {
        Sucursal sucursal1=sucursalService.save(sucursal);
        return new ResponseEntity<>(sucursal1, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSucursal(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        sucursalService.update(id, sucursal);
        return new ResponseEntity<>("Sucursal actualizada con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        sucursalService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activada" : "desactivada";
        return new ResponseEntity<>("Sucursal " + statusMessage + " con éxito", HttpStatus.OK);
    }

    @GetMapping("/compania/{companiaId}")
    public ResponseEntity<List<Sucursal>> findSucursalesByCompaniaId(@PathVariable Long companiaId) {
        List<Sucursal> sucursales = sucursalService.findCompaniaById(companiaId);
        return new ResponseEntity<>(sucursales, HttpStatus.OK);
    }
}
