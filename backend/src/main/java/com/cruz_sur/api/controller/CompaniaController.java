package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.service.ICompaniaService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<Compania> save(@RequestParam("file") MultipartFile file, @RequestParam("compania") String companiaJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Compania compania = objectMapper.readValue(companiaJson, Compania.class);

            Compania savedCompania = companiaService.save(compania, file);
            return new ResponseEntity<>(savedCompania, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Compania> update(@PathVariable Long id, @RequestBody Compania compania) {
        try {
            Compania updatedCompania = companiaService.update(id, compania);
            return new ResponseEntity<>(updatedCompania, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Compania>> all() {
        List<Compania> companias = companiaService.all();
        return new ResponseEntity<>(companias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compania> byId(@PathVariable Long id) {
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

    @PutMapping("/{id}/imagen")
    public ResponseEntity<Compania> updateCompaniaImage(@PathVariable Long id,
                                                        @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Compania> companiaOpt = companiaService.byId(id);

        if (companiaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Compania updatedCompania = companiaService.updateCompaniaImage(file, companiaOpt.get());
        return ResponseEntity.ok(updatedCompania);
    }
}
