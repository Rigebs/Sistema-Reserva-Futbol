package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.ReservaDTO;
import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.model.MetodoPago;
import com.cruz_sur.api.model.Horario;
import com.cruz_sur.api.model.Campo;
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
    private ClienteRepository clienteRepository;

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

    @Transactional
    @Override
    public Reserva createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new RuntimeException("No associated client for the authenticated user");
        }

        // Validar el tipo de comprobante según las reglas
        Character tipoComprobante = reservaDTO.getTipoComprobante();
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

        // Resto del método para crear la reserva...
        MetodoPago metodoPago = metodoPagoRepository.findById(reservaDTO.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        Horario horario = horarioRepository.findById(reservaDTO.getHorarioId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        BigDecimal subtotal = detallesVenta.stream()
                .map(detalle -> {
                    Campo campo = campoRepository.findById(detalle.getCampoId())
                            .orElseThrow(() -> new RuntimeException("Campo not found for id: " + detalle.getCampoId()));
                    return campo.getPrecio();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuento = BigDecimal.ZERO;
        if (reservaDTO.getDescuento() != null && reservaDTO.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            totalDescuento = subtotal.multiply(reservaDTO.getDescuento()).divide(BigDecimal.valueOf(100));
        }

        BigDecimal subtotalAfterDiscount = subtotal.subtract(totalDescuento);
        BigDecimal igvAmount = BigDecimal.ZERO;
        if (reservaDTO.getIgv() != null && reservaDTO.getIgv().compareTo(BigDecimal.ZERO) > 0) {
            igvAmount = subtotalAfterDiscount.multiply(reservaDTO.getIgv()).divide(BigDecimal.valueOf(100));
        }
        BigDecimal total = subtotalAfterDiscount.add(igvAmount);

        LocalDateTime now = LocalDateTime.now();

        Reserva reserva = Reserva.builder()
                .fecha(reservaDTO.getFecha())
                .descuento(reservaDTO.getDescuento())
                .igv(reservaDTO.getIgv())
                .total(total)
                .totalDescuento(totalDescuento)
                .subtotal(subtotal)
                .tipoComprobante(tipoComprobante) // Using the correct variable
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
        detallesVenta.forEach(detalle -> detalleVentaService.createDetalleVenta(detalle, reserva));

        return reserva;
    }

}
