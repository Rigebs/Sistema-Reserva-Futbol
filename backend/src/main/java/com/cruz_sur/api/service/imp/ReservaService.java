package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.ReservaDTO;
import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private CampoRepository campoRepository;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional
    public Reserva createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new RuntimeException("No associated client for the authenticated user");
        }

        validateTipoComprobante(cliente, reservaDTO.getTipoComprobante());
        MetodoPago metodoPago = metodoPagoRepository.findById(reservaDTO.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        Horario horario = horarioRepository.findById(reservaDTO.getHorarioId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        BigDecimal subtotal = calculateSubtotal(detallesVenta);
        BigDecimal totalDescuento = calculateDiscount(subtotal, reservaDTO.getDescuento());
        BigDecimal igvAmount = calculateIgv(subtotal.subtract(totalDescuento), reservaDTO.getIgv());
        BigDecimal total = subtotal.subtract(totalDescuento).add(igvAmount);

        LocalDateTime now = LocalDateTime.now();
        Reserva reserva = Reserva.builder()
                .fecha(reservaDTO.getFecha())
                .descuento(reservaDTO.getDescuento())
                .igv(reservaDTO.getIgv())
                .total(total)
                .totalDescuento(totalDescuento)
                .subtotal(subtotal)
                .tipoComprobante(reservaDTO.getTipoComprobante())
                .cliente(cliente)
                .usuario(usuario)
                .metodoPago(metodoPago)
                .horario(horario)
                .estado('1')
                .usuarioCreacion(authenticatedUsername)
                .fechaCreacion(now)
                .usuarioModificacion(authenticatedUsername)
                .fechaModificacion(now)
                .build();

        reservaRepository.save(reserva);
        createComprobante(reserva, authenticatedUsername, now);

        detallesVenta.forEach(detalle -> detalleVentaService.createDetalleVenta(detalle, reserva));
        return reserva;
    }

    private void validateTipoComprobante(Cliente cliente, Character tipoComprobante) {
        if (tipoComprobante == null) {
            throw new RuntimeException("Tipo de comprobante no especificado");
        }

        boolean isEmpresa = cliente.getEmpresa() != null;
        boolean isPersona = cliente.getPersona() != null;

        switch (tipoComprobante) {
            case 'F':
                if (!isEmpresa) {
                    throw new RuntimeException("El tipo de comprobante 'F' solo es válido para empresas.");
                }
                break;
            case 'B':
                if (!isPersona) {
                    throw new RuntimeException("El tipo de comprobante 'B' solo es válido para personas.");
                }
                break;
            case 'T':
                if (!isEmpresa && !isPersona) {
                    throw new RuntimeException("El tipo de comprobante 'T' es válido solo si existe una empresa o una persona.");
                }
                break;
            default:
                throw new RuntimeException("Tipo de comprobante no válido: debe ser 'F', 'B' o 'T'.");
        }
    }

    private BigDecimal calculateSubtotal(List<DetalleVentaDTO> detallesVenta) {
        return detallesVenta.stream()
                .map(detalle -> {
                    Campo campo = campoRepository.findById(detalle.getCampoId())
                            .orElseThrow(() -> new RuntimeException("Campo not found for id: " + detalle.getCampoId()));
                    return campo.getPrecio();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal descuento) {
        return (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0)
                ? subtotal.multiply(descuento).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

    private BigDecimal calculateIgv(BigDecimal subtotalAfterDiscount, BigDecimal igv) {
        return (igv != null && igv.compareTo(BigDecimal.ZERO) > 0)
                ? subtotalAfterDiscount.multiply(igv).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

    private void createComprobante(Reserva reserva, String authenticatedUsername, LocalDateTime now) {
        String serie;
        String numero = generateNumeroComprobante(reserva.getTipoComprobante());

        switch (reserva.getTipoComprobante()) {
            case 'B':
                serie = "B001";
                Boleta boleta = Boleta.builder()
                        .serie(serie)
                        .numero(numero)
                        .reserva(reserva)
                        .estado('1')
                        .usuarioCreacion(authenticatedUsername)
                        .fechaCreacion(now)
                        .usuarioModificacion(authenticatedUsername)
                        .fechaModificacion(now)
                        .build();
                boletaRepository.save(boleta);
                break;
            case 'F':
                serie = "F001";
                Factura factura = Factura.builder()
                        .serie(serie)
                        .numero(numero)
                        .reserva(reserva)
                        .estado('1')
                        .usuarioCreacion(authenticatedUsername)
                        .fechaCreacion(now)
                        .usuarioModificacion(authenticatedUsername)
                        .fechaModificacion(now)
                        .build();
                facturaRepository.save(factura);
                break;
            case 'T':
                serie = "T001";
                Ticket ticket = Ticket.builder()
                        .serie(serie)
                        .numero(numero)
                        .reserva(reserva)
                        .estado('1')
                        .usuarioCreacion(authenticatedUsername)
                        .fechaCreacion(now)
                        .usuarioModificacion(authenticatedUsername)
                        .fechaModificacion(now)
                        .build();
                ticketRepository.save(ticket);
                break;
            default:
                throw new IllegalArgumentException("Invalid tipoComprobante: " + reserva.getTipoComprobante());
        }
    }

    private String generateNumeroComprobante(Character tipoComprobante) {
        long count = switch (tipoComprobante) {
            case 'B' -> boletaRepository.count();
            case 'F' -> facturaRepository.count();
            case 'T' -> ticketRepository.count();
            default -> throw new IllegalArgumentException("Invalid tipoComprobante: " + tipoComprobante);
        };
        return String.format("%06d", count + 1);
    }
}
