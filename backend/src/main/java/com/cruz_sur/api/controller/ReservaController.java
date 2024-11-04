package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.responses.TotalReservasResponse;
import com.cruz_sur.api.service.IReservaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reservas")
@AllArgsConstructor
public class ReservaController {

    private final IReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody ReservaRequest request) {
        ReservaResponseDTO reservaResponse = reservaService.createReserva(request.getReservaDTO(), request.getDetallesVenta());
        return ResponseEntity.ok(reservaResponse);
    }

    @GetMapping("/ventas")
    public List<VentaDTO> getVentas() {
        return reservaService.getVentasByUsuario();
    }

    @GetMapping("/dashboard/total-reservas")
    public ResponseEntity<TotalReservasResponse> getTotalReservas() {
        TotalReservasResponse totalResponse = reservaService.getTotalReservas();
        return ResponseEntity.ok(totalResponse);
    }

    @GetMapping("/dashboard/total-sede")
    public ResponseEntity<TotalReservasResponse> getTotalReservasSede() {
        TotalReservasResponse totalResponse = reservaService.getTotalReservasSede();
        return ResponseEntity.ok(totalResponse);
    }

    @GetMapping("/sede")
    public ResponseEntity<?> getReservasForLoggedUser() {
        try {
            List<ReservaDisplayDTO> reservas = reservaService.getReservasForLoggedUser();
            return ResponseEntity.ok(reservas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
