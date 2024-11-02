package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ComprobanteService {

    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;
    private final TicketRepository ticketRepository;
    private final NumeroComprobanteGenerator numeroComprobanteGenerator;

    public void createComprobante(Reserva reserva, User usuario, LocalDateTime now, Campo campo) {
        String serie;
        User userSede = campo != null && campo.getUsuario() != null && campo.getUsuario().getSede() != null
                ? campo.getUsuario()
                : null;

        String numero = numeroComprobanteGenerator.generate(userSede.getId(), reserva.getTipoComprobante());

        switch (reserva.getTipoComprobante()) {
            case 'B':
                serie = "B001";
                saveBoleta(reserva, usuario, now, userSede, serie, numero);
                break;
            case 'F':
                serie = "F001";
                saveFactura(reserva, usuario, now, userSede, serie, numero);
                break;
            case 'T':
                serie = "T001";
                saveTicket(reserva, usuario, now, userSede, serie, numero);
                break;
            default:
                throw new IllegalArgumentException("Invalid tipoComprobante: " + reserva.getTipoComprobante());
        }
    }

    private void saveBoleta(Reserva reserva, User usuario, LocalDateTime now, User userSede, String serie, String numero) {
        Boleta boleta = Boleta.builder()
                .serie(serie)
                .numero(numero)
                .reserva(reserva)
                .estado('1')
                .usuarioCreacion(usuario.getUsername())
                .fechaCreacion(now)
                .usuarioModificacion(usuario.getUsername())
                .fechaModificacion(now)
                .usuario(userSede)
                .build();
        boletaRepository.save(boleta);
    }

    private void saveFactura(Reserva reserva, User usuario, LocalDateTime now, User userSede, String serie, String numero) {
        Factura factura = Factura.builder()
                .serie(serie)
                .numero(numero)
                .reserva(reserva)
                .estado('1')
                .usuarioCreacion(usuario.getUsername())
                .fechaCreacion(now)
                .usuarioModificacion(usuario.getUsername())
                .fechaModificacion(now)
                .usuario(userSede)
                .build();
        facturaRepository.save(factura);
    }

    private void saveTicket(Reserva reserva, User usuario, LocalDateTime now, User userSede, String serie, String numero) {
        Ticket ticket = Ticket.builder()
                .serie(serie)
                .numero(numero)
                .reserva(reserva)
                .estado('1')
                .usuarioCreacion(usuario.getUsername())
                .fechaCreacion(now)
                .usuarioModificacion(usuario.getUsername())
                .fechaModificacion(now)
                .usuario(userSede)
                .build();
        ticketRepository.save(ticket);
    }
}