package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.responses.ReniecSunatResponse;
import com.cruz_sur.api.service.IEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController {
    private final IEmpresaService empresaService;
    private final ReniecSunatResponse reniecSunatResponse;

    @PostMapping("/consultar-ruc")
    public ResponseEntity<Map<String, Object>> consultarRuc(@RequestParam String ruc) {
        Map<String, Object> empresaData = reniecSunatResponse.getRuc(ruc);
        if (!empresaData.isEmpty()) {
            return ResponseEntity.ok(empresaData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No se encontraron datos para el RUC"));
        }
    }
    @GetMapping
    public ResponseEntity<List<Empresa>> getAllEmpresas() {
        List<Empresa> empresas = empresaService.all();
        return new ResponseEntity<>(empresas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmpresaById(@PathVariable Long id) {
        Empresa empresa = empresaService.byId(id).orElse(null);
        return empresa != null
                ? new ResponseEntity<>(empresa, HttpStatus.OK)
                : new ResponseEntity<>("Empresa no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createEmpresa(@RequestBody Empresa empresa) {
        Empresa newEmpresa = empresaService.save(empresa);
        return new ResponseEntity<>("Empresa guardada con éxito: " + newEmpresa.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEmpresa(@PathVariable Long id, @RequestBody Empresa empresa) {
        try {
            Empresa updatedEmpresa = empresaService.update(id, empresa);
            return new ResponseEntity<>(updatedEmpresa, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            empresaService.changeStatus(id, status);
            String statusMessage = status == 1 ? "activada" : "desactivada";
            return new ResponseEntity<>("Empresa " + statusMessage + " con éxito", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
