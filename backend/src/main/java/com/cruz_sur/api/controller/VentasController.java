package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.ContarReservasDTO;
import com.cruz_sur.api.dto.VentasMensualesDTO;
import com.cruz_sur.api.service.IVentasService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@AllArgsConstructor
public class VentasController {

    private final IVentasService ventasService;

    @GetMapping
    public ResponseEntity<List<VentasMensualesDTO>> all() {
        List<VentasMensualesDTO> ventasMensuales = ventasService.all();
        return ResponseEntity.ok(ventasMensuales);
    }

    @GetMapping("/reservas/total")
    public ResponseEntity<ContarReservasDTO> obtenerTotalReservas(@RequestParam String fecha) {
        ContarReservasDTO totalReservas = ventasService.countTotalReservas(fecha);
        return ResponseEntity.ok(totalReservas);
    }

    @GetMapping("/reservas/campos-reservados")
    public ResponseEntity<List<ContarReservasDTO>> obtenerCamposReservados() {
        List<ContarReservasDTO> camposReservados = ventasService.countCamposReservados();
        return ResponseEntity.ok(camposReservados);
    }

    @GetMapping("/reservas/diarias")
    public ResponseEntity<ContarReservasDTO> obtenerTotalReservasDiarias(@RequestParam String fecha) {
        ContarReservasDTO totalReservasDiarias = ventasService.countTotalReservasDiarias(fecha);
        return ResponseEntity.ok(totalReservasDiarias);
    }
}
