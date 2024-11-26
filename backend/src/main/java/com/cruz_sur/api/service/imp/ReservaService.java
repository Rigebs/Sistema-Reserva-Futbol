package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.controller.AvailabilityController;
import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.responses.TotalReservasResponse;
import com.cruz_sur.api.service.IReservaService;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final DetalleVentaRepository detalleVentaRepository;
    private final ComprobanteService comprobanteService;
    private final CampoRepository campoRepository;
    private final ReservaValidationService reservaValidationService;
    private final CampoAvailabilityService campoAvailabilityService;
    private final ReservaResponseBuilder reservaResponseBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final EmailService emailService;


    @Transactional
    @Override
    public ReservaResponseDTO createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta) {
        // Obtener el usuario autenticado
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Obtener cliente
        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new RuntimeException("No associated client for the authenticated user");
        }

        // Validación del tipo de comprobante
        reservaValidationService.validateTipoComprobante(cliente, reservaDTO.getTipoComprobante());

        // Obtener el método de pago
        MetodoPago metodoPago = metodoPagoRepository.findById(reservaDTO.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        LocalDateTime now = LocalDateTime.now();

        // **1. Validar disponibilidad de los campos antes de crear la reserva**
        validarDisponibilidadCampos(detallesVenta);

        // **2. Crear la reserva**
        Reserva reserva = crearReserva(reservaDTO, cliente, usuario, metodoPago, now);
        reservaRepository.save(reserva);

        // **3. Crear los detalles de la venta**
        for (DetalleVentaDTO detalleDTO : detallesVenta) {
            detalleVentaService.createDetalleVenta(detalleDTO, reserva);
        }

        // **4. Enviar el correo al dueño del campo después de crear la reserva**
        enviarCorreosALosDuenosDeCampo(detallesVenta, reserva);

        return reservaResponseBuilder.build(reserva);
    }

    private void validarDisponibilidadCampos(List<DetalleVentaDTO> detallesVenta) {
        for (DetalleVentaDTO detalleDTO : detallesVenta) {
            Long campoId = detalleDTO.getCampoId();
            Campo campo = campoRepository.findById(campoId)
                    .orElseThrow(() -> new RuntimeException("Campo not found for id: " + campoId));

            LocalDate detalleFecha = detalleDTO.getFecha();
            boolean available = campoAvailabilityService.isCampoAvailable(
                    campoId, detalleFecha, detalleDTO.getHoraInicio(), detalleDTO.getHoraFinal());

            if (!available) {
                throw new RuntimeException("Campo not available for the specified time range");
            }
        }
    }

    private Reserva crearReserva(ReservaDTO reservaDTO, Cliente cliente, User usuario, MetodoPago metodoPago, LocalDateTime now) {
        return Reserva.builder()
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
                .estado('0')  // Estado inicial
                .usuarioCreacion(usuario.getUsername())
                .fechaCreacion(now)
                .usuarioModificacion(usuario.getUsername())
                .fechaModificacion(now)
                .build();
    }

    private void enviarCorreosALosDuenosDeCampo(List<DetalleVentaDTO> detallesVenta, Reserva reserva) {
        for (DetalleVentaDTO detalleDTO : detallesVenta) {
            Long campoId = detalleDTO.getCampoId();
            Campo campo = campoRepository.findById(campoId)
                    .orElseThrow(() -> new RuntimeException("Campo not found for id: " + campoId));

            // Obtener usuario del dueño del campo
            User campoUsuario = campo.getUsuario();
            if (campoUsuario != null) {
                sendVerificationEmail(reserva, campoUsuario); // Enviar el correo al dueño del campo
            }
        }
    }


    private void sendVerificationEmail(Reserva reserva, User user) {
        // Include key reservation details in the subject
        String subject = "ID: " + reserva.getId() +
                ", Date: " + reserva.getFecha() +
                ", Total: " + reserva.getTotal();

        // Create the body with a more detailed message
        String htmlMessage = "<html>"
                + "<head><style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                + "h2 { color: #4CAF50; }"
                + "table { width: 100%; border-collapse: collapse; margin-top: 20px; }"
                + "td, th { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }"
                + "th { background-color: #f2f2f2; }"
                + "</style></head>"
                + "<body>"
                + "<h2>Reservation Confirmation</h2>"
                + "<p>Dear " + user.getUsername() + ",</p>"
                + "<p>Thank you for your reservation! Below are the details of your new booking:</p>"
                + "<table>"
                + "<tr><th>Reservation ID</th><td>" + reserva.getId() + "</td></tr>"
                + "<tr><th>Date</th><td>" + reserva.getFecha() + "</td></tr>"
                + "<tr><th>Subtotal</th><td>" + reserva.getSubtotal() + "</td></tr>"
                + "<tr><th>Total</th><td>" + reserva.getTotal() + "</td></tr>"
                + "<tr><th>Payment Method</th><td>" + reserva.getMetodoPago().getNombre() + "</td></tr>"
                + "<tr><th>Client ID</th><td>" + reserva.getCliente().getId() + "</td></tr>"
                + "</table>"
                + "<p>If you have any questions or need further assistance, please don't hesitate to contact our support team.</p>"
                + "<p>Best regards,</p>"
                + "<p>Your Company Name</p>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    @Transactional
    @Override
    public ReservaResponseDTO validarPagoReserva(Long reservaId, BigDecimal montoPago) {
        // Busca la reserva por su ID
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verifica si la reserva ya está confirmada
        if (reserva.getEstado() == '1') {
            throw new RuntimeException("La reserva ya está confirmada.");
        }

        // Compara el monto pagado con el total de la reserva
        if (reserva.getTotal().compareTo(montoPago) == 0) {
            // Cambia el estado de la reserva a confirmado
            reserva.setEstado('1');
            reservaRepository.save(reserva);

            // Actualiza el estado de los detalles de venta asociados
            actualizarEstadoDetallesVenta(reservaId);

            return buildReservaResponse(reserva, BigDecimal.ZERO); // Sin cambio
        } else if (reserva.getTotal().compareTo(montoPago) < 0) {
            // Si el monto es mayor, calculamos el cambio
            BigDecimal cambio = montoPago.subtract(reserva.getTotal());

            // Cambia el estado de la reserva a confirmado
            reserva.setEstado('1');
            reservaRepository.save(reserva);

            // Actualiza el estado de los detalles de venta asociados
            actualizarEstadoDetallesVenta(reservaId);

            // Devolver la reserva con el cambio
            return buildReservaResponse(reserva, cambio);
        } else {
            // Si el monto es menor, lanza un error
            throw new RuntimeException("El monto del pago no coincide con el total de la reserva.");
        }
    }

    // Método para actualizar el estado de los detalles de venta
    private void actualizarEstadoDetallesVenta(Long reservaId) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_Id(reservaId);
        for (DetalleVenta detalle : detalles) {
            detalle.setEstado('1'); // Cambia el estado a confirmado
        }
        detalleVentaRepository.saveAll(detalles); // Guarda los cambios en los detalles
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
                rs.getTimestamp(3).toLocalDateTime(),
                rs.getBigDecimal(4),
                rs.getBigDecimal(5),
                rs.getString(6)
        ));
    }

    @Override
    public boolean isReservaActive(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId).orElse(null);

        if (reserva == null) {
            return false;
        }

        return reserva.getEstado() == '1';  // Assuming '1' means active
    }
}
