package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.service.ICompaniaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/companias")
@AllArgsConstructor
public class CompaniaController {

    private final ICompaniaService companiaService;

    @PostMapping
    public ResponseEntity<Compania> saveCompania(@RequestParam("file") MultipartFile file, @RequestBody Compania compania) {
        try {
            Compania savedCompania = companiaService.save(compania, file);
            return new ResponseEntity<>(savedCompania, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compania> updateCompania(@PathVariable Long id, @RequestBody Compania compania) {
        try {
            Compania updatedCompania = companiaService.update(id, compania);
            return new ResponseEntity<>(updatedCompania, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Compania>> getAllCompanias() {
        List<Compania> companias = companiaService.all();
        return new ResponseEntity<>(companias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compania> getCompaniaById(@PathVariable Long id) {
        Optional<Compania> compania = companiaService.byId(id);
        return compania.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Compania> changeCompaniaStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            Compania updatedCompania = companiaService.changeStatus(id, status);
            return new ResponseEntity<>(updatedCompania, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-image/{id}")
    public ResponseEntity<Compania> updateCompaniaImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Compania compania = companiaService.byId(id)
                    .orElseThrow(() -> new RuntimeException("Compania no encontrada"));
            Compania updatedCompania = companiaService.updateCompaniaImage(file, compania);
            return new ResponseEntity<>(updatedCompania, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
