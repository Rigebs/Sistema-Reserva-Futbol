package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.MetodoPago;
import com.cruz_sur.api.service.IMetodoPagoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pago")
@AllArgsConstructor
public class MetodoPagoController {

    private final IMetodoPagoService metodoPagoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> getAllMetodosPago() {
        List<MetodoPago> metodosPago = metodoPagoService.all();
        return new ResponseEntity<>(metodosPago, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMetodoPagoById(@PathVariable Long id) {
        MetodoPago metodoPago = metodoPagoService.byId(id).orElse(null);
        return metodoPago != null
                ? new ResponseEntity<>(metodoPago, HttpStatus.OK)
                : new ResponseEntity<>("Método de pago no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createMetodoPago(@RequestBody MetodoPago metodoPago) {
        metodoPagoService.save(metodoPago);
        return new ResponseEntity<>("Método de pago creado con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMetodoPago(@PathVariable Long id, @RequestBody MetodoPago metodoPago) {
        metodoPagoService.update(id, metodoPago);
        return new ResponseEntity<>("Método de pago actualizado con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        metodoPagoService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Método de pago " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
