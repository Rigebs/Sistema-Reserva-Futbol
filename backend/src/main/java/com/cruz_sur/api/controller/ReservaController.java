package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.repository.ReservaRepository;
import com.cruz_sur.api.responses.TotalReservasResponse;
import com.cruz_sur.api.service.IReservaService;
import com.cruz_sur.api.service.imp.ReservaResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cruz_sur.api.responses.PagoResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/reservas")
@AllArgsConstructor
public class ReservaController {

    private final IReservaService reservaService;
    private final ReservaRepository reservaRepository;
    private final ReservaResponseBuilder reservaResponseBuilder;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody ReservaRequest request) {
        ReservaResponseDTO reservaResponse = reservaService.createReserva(request.getReservaDTO(), request.getDetallesVenta());
        return ResponseEntity.ok(reservaResponse);
    }
    @PostMapping("/validar-pago")
    public ResponseEntity<?> validarPago(@RequestParam Long reservaId, @RequestParam BigDecimal montoPago) {
        try {
            // Llamamos al servicio para validar el pago
            ReservaResponseDTO reservaResponse = reservaService.validarPagoReserva(reservaId, montoPago);

            // Si el pago es correcto, devolvemos la respuesta con detalles
            return ResponseEntity.ok(reservaResponse);
        } catch (RuntimeException e) {
            // En caso de error, devolvemos un error con el mensaje
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
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
    
    @GetMapping("/{reservaId}/is-active")
    public ResponseEntity<PagoResponse> isReservaActive(@PathVariable Long reservaId) {
        boolean isActive = reservaService.isReservaActive(reservaId);
        return ResponseEntity.ok(new PagoResponse(isActive));
    }

    @GetMapping("/cliente/{id}")
    public ReservaResponseDTO getReservaById(@PathVariable Long id) {
        Optional<Reserva> reservaOptional = reservaRepository.findById(id);
        if (reservaOptional.isEmpty()) {
            throw new RuntimeException("Reserva not found with id: " + id);
        }
        return reservaResponseBuilder.build(reservaOptional.get());
    }
}
