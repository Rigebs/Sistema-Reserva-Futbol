package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.CampoDTO;
import com.cruz_sur.api.dto.CampoSedeDTO;
import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/campos")
@AllArgsConstructor
public class CampoController {

    private final ICampoService campoService;

    @GetMapping
    public ResponseEntity<List<CampoDTO>> all() {
        List<CampoDTO> campos = campoService.all();
        return new ResponseEntity<>(campos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> byId(@PathVariable Long id) {
        Optional<CampoDTO> campoDTO = campoService.byId(id);
        return campoDTO.<ResponseEntity<Object>>map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("Campo no encontrado", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{usuarioId}/with-sede")
    public ResponseEntity<List<CampoDTO>> getCamposByUsuarioIdWithSede(@PathVariable Long usuarioId) {
        List<CampoDTO> campos = campoService.findByUsuarioIdWithSede(usuarioId);
        return campos.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(campos, HttpStatus.OK);
    }

    @GetMapping("/with-sede")
    public ResponseEntity<List<CampoSedeDTO>> getAllCamposWithSede() {
        List<CampoSedeDTO> campos = campoService.findAllSedeInfo();
        return campos.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(campos, HttpStatus.OK);
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
