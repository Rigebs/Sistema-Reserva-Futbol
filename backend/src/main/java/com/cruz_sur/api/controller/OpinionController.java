package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.OpinionDTO;
import com.cruz_sur.api.dto.OpinionSummaryDTO;
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

    // Aseguramos que solo el autor de la opinión pueda actualizarla
    @PutMapping("/{id}")
    public ResponseEntity<OpinionDTO> updateOpinion(@PathVariable Long id, @RequestBody OpinionDTO opinionDTO) {
        OpinionDTO updatedOpinion = opinionService.updateOpinion(id, opinionDTO);
        return ResponseEntity.ok(updatedOpinion);
    }

    @GetMapping("/resumen/{companiaId}")
    public OpinionSummaryDTO summaryOpinion(@PathVariable Long companiaId) {
        return opinionService.summaryOpinion(companiaId);
    }
}
