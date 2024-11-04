package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.config.GlobalExceptionHandler;
import com.cruz_sur.api.controller.AvailabilityController;
import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.responses.TotalReservasResponse;
import com.cruz_sur.api.service.IReservaService;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private final ReservaCalculations reservaCalculations;
    private final DetalleVentaRepository detalleVentaRepository;
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

        BigDecimal subtotal = reservaCalculations.calculateSubtotal(detallesVenta);
        BigDecimal totalDescuento = reservaCalculations.calculateDiscount(subtotal, reservaDTO.getDescuento());
        BigDecimal igvAmount = reservaCalculations.calculateIgv(subtotal.subtract(totalDescuento), reservaDTO.getIgv());
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
                .estado('1')
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

        String query = "EXEC ALL_RESERVAS @UserId = ?, @ACTION = 'C'";
        Long total = jdbcTemplate.queryForObject(query, new Object[]{userId}, Long.class);

        return new TotalReservasResponse(total);
    }


    @Override
    public List<ReservaDisplayDTO> getReservasForLoggedUser() {
        Long userId = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String query = "EXEC ALL_RESERVAS ?, @ACTION = 'L'";

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
