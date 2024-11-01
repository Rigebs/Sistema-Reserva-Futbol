package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.ReservaRequest;
import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> createReserva(@RequestBody ReservaRequest request) {
        Reserva reserva = reservaService.createReserva(request.getReservaDTO(), request.getDetallesVenta());
        return ResponseEntity.ok(reserva);
    }

}
