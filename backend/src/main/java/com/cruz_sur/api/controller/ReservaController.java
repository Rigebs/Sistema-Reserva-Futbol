package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.ReservaRequest;
import com.cruz_sur.api.dto.ReservaResponseDTO;
import com.cruz_sur.api.dto.VentaDTO;
import com.cruz_sur.api.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

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
    public ResponseEntity<Integer> getTotalReservas() {
        int total = reservaService.getTotalReservas();
        return ResponseEntity.ok(total);
    }


}
