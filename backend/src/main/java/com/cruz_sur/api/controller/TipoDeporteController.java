package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.TipoDeporte;
import com.cruz_sur.api.service.ITipoDeporteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/tipo-deporte")
public class TipoDeporteController {
    private final ITipoDeporteService tipoDeporteService;

    @GetMapping
    public ResponseEntity<List<TipoDeporte>> getAllTipoDeportes() {
        List<TipoDeporte> tipoDeportes = tipoDeporteService.findAll();
        return new ResponseEntity<>(tipoDeportes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTipoDeporteById(@PathVariable Long id) {
        TipoDeporte tipoDeporte = tipoDeporteService.findById(id).orElse(null);
        return tipoDeporte != null
                ? new ResponseEntity<>(tipoDeporte, HttpStatus.OK)
                : new ResponseEntity<>("Tipo de deporte no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<TipoDeporte> save(@RequestBody TipoDeporte tipoDeporte) {
        TipoDeporte newTipoDeporte = tipoDeporteService.save(tipoDeporte);
        return new ResponseEntity<>(newTipoDeporte, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody TipoDeporte tipoDeporte) {
        try {
            TipoDeporte updatedTipoDeporte = tipoDeporteService.update(id, tipoDeporte);
            return new ResponseEntity<>(updatedTipoDeporte, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            tipoDeporteService.changeStatus(id, status);
            String statusMessage = status == 1 ? "activado" : "desactivado";
            return new ResponseEntity<>("Tipo de deporte " + statusMessage + " con éxito", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
