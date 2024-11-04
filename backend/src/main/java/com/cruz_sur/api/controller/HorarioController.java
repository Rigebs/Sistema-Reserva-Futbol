package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Horario;
import com.cruz_sur.api.service.IHorarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
@AllArgsConstructor
public class HorarioController {

    private final IHorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<Horario>> all() {
        List<Horario> horarios = horarioService.all();
        return new ResponseEntity<>(horarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> byId(@PathVariable Long id) {
        Horario horario = horarioService.byId(id).orElse(null);
        return horario != null
                ? new ResponseEntity<>(horario, HttpStatus.OK)
                : new ResponseEntity<>("Horario no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody Horario horario) {
        horarioService.save(horario);
        return new ResponseEntity<>("Horario creado con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Horario horario) {
        horarioService.update(id, horario);
        return new ResponseEntity<>("Horario actualizado con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        horarioService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Horario " + statusMessage + " con éxito", HttpStatus.OK);
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> saveBulk() {
        for (int hour = 0; hour < 24; hour++) {
            Horario horario = new Horario();
            horario.setHoraInicio(Time.valueOf(LocalTime.of(hour, 0)));
            horario.setHoraFinal(Time.valueOf(LocalTime.of(hour, 59)));
            horario.setEstado('1'); // Set default status to active
            horarioService.save(horario);
        }
        return new ResponseEntity<>("24 Horarios creados con éxito", HttpStatus.CREATED);
    }
}
