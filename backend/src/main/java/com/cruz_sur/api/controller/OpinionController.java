package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.OpinionDTO;
import com.cruz_sur.api.service.IOpinionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opiniones")
@AllArgsConstructor
public class OpinionController {

    private final IOpinionService opinionService;

    @PostMapping
    public ResponseEntity<OpinionDTO> saveOpinion(@RequestBody OpinionDTO opinionDTO) {
        OpinionDTO savedOpinion = opinionService.saveOpinion(opinionDTO);
        return ResponseEntity.ok(savedOpinion);
    }

    @GetMapping("/compania/{companiaId}")
    public ResponseEntity<List<OpinionDTO>> getOpinionsByCompania(@PathVariable Long companiaId) {
        List<OpinionDTO> opiniones = opinionService.getOpinionsByCompania(companiaId);
        return ResponseEntity.ok(opiniones);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OpinionDTO>> getOpinionsByAuthenticatedUser() {
        List<OpinionDTO> opiniones = opinionService.getOpinionsByAuthenticatedUser();
        return ResponseEntity.ok(opiniones);
    }


    @PutMapping("/{id}")
    public ResponseEntity<OpinionDTO> updateOpinion(@PathVariable Long id, @RequestBody OpinionDTO opinionDTO) {
        opinionDTO.setId(id); // Asignar el ID de la opinión desde el parámetro de ruta
        OpinionDTO updatedOpinion = opinionService.updateOpinion(opinionDTO);
        return ResponseEntity.ok(updatedOpinion);
    }
}
