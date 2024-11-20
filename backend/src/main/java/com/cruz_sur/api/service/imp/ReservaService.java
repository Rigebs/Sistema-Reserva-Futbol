package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.controller.AvailabilityController;
import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.responses.TotalReservasResponse;
import com.cruz_sur.api.service.IReservaService;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservaService implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UserRepository userRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final DetalleVentaService detalleVentaService;
    private final ComprobanteService comprobanteService;
    private final ReservaValidationService reservaValidationService;
    private final CampoRepository campoRepository;
    private final AvailabilityController availabilityController;
    private final ReservaResponseBuilder reservaResponseBuilder;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public ReservaResponseDTO createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new RuntimeException("No associated client for the authenticated user");
        }

        reservaValidationService.validateTipoComprobante(cliente, reservaDTO.getTipoComprobante());
        MetodoPago metodoPago = metodoPagoRepository.findById(reservaDTO.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        LocalDateTime now = LocalDateTime.now();
        Reserva reserva = Reserva.builder()
                .fecha(reservaDTO.getFecha())
                .descuento(reservaDTO.getDescuento())
                .igv(reservaDTO.getIgv())
                .total(reservaDTO.getTotal())
                .totalDescuento(reservaDTO.getTotalDescuento())
                .subtotal(reservaDTO.getSubtotal())
                .tipoComprobante(reservaDTO.getTipoComprobante())
                .cliente(cliente)
                .usuario(usuario)
                .metodoPago(metodoPago)
                .estado('0')
                .usuarioCreacion(authenticatedUsername)
                .fechaCreacion(now)
                .usuarioModificacion(authenticatedUsername)
                .fechaModificacion(now)
                .build();

        boolean comprobanteCreated = false;

        // Primero, verifica la disponibilidad de todos los campos en detallesVenta
        for (DetalleVentaDTO detalleDTO : detallesVenta) {
            Long campoId = detalleDTO.getCampoId();
            Campo campo = campoRepository.findById(campoId)
                    .orElseThrow(() -> new RuntimeException("Campo not found for id: " + campoId));

            boolean available = availabilityController.checkCampoAvailability(
                    campoId, reservaDTO.getFecha(), detalleDTO.getHoraInicio(), detalleDTO.getHoraFinal());

            if (!available) {
                throw new RuntimeException("Campo not available for the specified time range");
            }
        }

        // Si todos los campos están disponibles, guarda la reserva y detalles
        reservaRepository.save(reserva);

        for (DetalleVentaDTO detalleDTO : detallesVenta) {
            Long campoId = detalleDTO.getCampoId();
            Campo campo = campoRepository.findById(campoId)
                    .orElseThrow(() -> new RuntimeException("Campo not found for id: " + campoId));

            detalleVentaService.createDetalleVenta(detalleDTO, reserva);
            availabilityController.notifyCampoStatusChange(campo);

            if (!comprobanteCreated) {
                comprobanteService.createComprobante(reserva, usuario, now, campo);
                comprobanteCreated = true;
            }
        }

        return reservaResponseBuilder.build(reserva);
    }
    @Transactional
    @Override
    public ReservaResponseDTO validarPagoReserva(Long reservaId, BigDecimal montoPago) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verifica si la reserva ya está confirmada
        if (reserva.getEstado() == '1') {
            throw new RuntimeException("La reserva ya está confirmada.");
        }

        // Compara el monto pagado con el total de la reserva
        if (reserva.getTotal().compareTo(montoPago) == 0) {
            reserva.setEstado('1'); // Cambia el estado a confirmado
            reservaRepository.save(reserva);

            return buildReservaResponse(reserva, BigDecimal.ZERO); // Sin cambio
        } else if (reserva.getTotal().compareTo(montoPago) < 0) {
            // Si el monto es mayor, calculamos el cambio
            BigDecimal cambio = montoPago.subtract(reserva.getTotal());

            // Devolver la reserva con el cambio
            return buildReservaResponse(reserva, cambio);
        } else {
            // Si el monto es menor, lanza un error
            throw new RuntimeException("El monto del pago no coincide con el total de la reserva.");
        }
    }

    private ReservaResponseDTO buildReservaResponse(Reserva reserva, BigDecimal cambio) {
        // Aquí construimos la respuesta con la reserva y el cambio (si hay)
        ReservaResponseDTO response = new ReservaResponseDTO();
        response.setReservaId(reserva.getId());
        response.setTotal(reserva.getTotal());
        response.setEstado(reserva.getEstado());
        response.setCambio(cambio);  // Si el cambio es 0, no es necesario mostrarlo en la respuesta

        return response;
    }

    @Override
    public List<VentaDTO> getVentasByUsuario() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reserva> reservas = reservaRepository.findByUsuario(usuario);

        return reservas.stream().map(reserva -> {
            VentaDTO ventaDTO = new VentaDTO();
            ventaDTO.setReservaId(reserva.getId());
            ventaDTO.setFecha(reserva.getFecha());
            ventaDTO.setTotal(reserva.getTotal());
            ventaDTO.setTipoComprobante(reserva.getTipoComprobante().toString());
            ventaDTO.setEstado(String.valueOf(reserva.getEstado()));

            return ventaDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public TotalReservasResponse getTotalReservas() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long total = reservaRepository.countByUsuario(usuario);
        return new TotalReservasResponse(total);
    }

    @Override
    public TotalReservasResponse getTotalReservasSede() {
        Long userId = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String query = "CALL ALL_RESERVAS(?, 'C')";
        Long total = jdbcTemplate.queryForObject(query, new Object[]{userId}, Long.class);

        return new TotalReservasResponse(total);
    }

    @Override
    public List<ReservaDisplayDTO> getReservasForLoggedUser() {
        Long userId = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String query = "CALL ALL_RESERVAS(?, 'L')";

        return jdbcTemplate.query(query, new Object[]{userId}, (rs, rowNum) -> new ReservaDisplayDTO(
                rs.getLong(1),
                rs.getString(2),
                rs.getDate(3).toLocalDate(),
                rs.getBigDecimal(4),
                rs.getBigDecimal(5),
                rs.getString(6)
        ));
    }


}
