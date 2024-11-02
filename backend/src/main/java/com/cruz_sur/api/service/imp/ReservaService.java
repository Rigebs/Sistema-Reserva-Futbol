package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import com.cruz_sur.api.service.IReservaService;
import lombok.AllArgsConstructor;
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
    private final ReservaCalculations reservaCalculations;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CampoRepository campoRepository;
    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;
    private final TicketRepository ticketRepository;

    @Transactional
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

        reservaRepository.save(reserva);

        Campo campo = null;
        if (!detallesVenta.isEmpty()) {
            Long campoId = detallesVenta.get(0).getCampoId();
            campo = campoRepository.findById(campoId)
                    .orElseThrow(() -> new RuntimeException("Campo not found for id: " + campoId));
        }

        comprobanteService.createComprobante(reserva, usuario, now, campo);
        detallesVenta.forEach(detalle -> detalleVentaService.createDetalleVenta(detalle, reserva));
        return buildReservaResponse(reserva);
    }

    private ReservaResponseDTO buildReservaResponse(Reserva reserva) {
        String cliente = reserva.getCliente().getPersona() != null
                ? reserva.getCliente().getPersona().getNombreCompleto()
                : reserva.getCliente().getEmpresa().getRazonSocial();

        String direccionCliente = reserva.getCliente().getPersona() != null
                ? reserva.getCliente().getPersona().getDireccion()
                : reserva.getCliente().getEmpresa().getDireccion();

        String identificacion = reserva.getCliente().getPersona() != null
                ? reserva.getCliente().getPersona().getDni()
                : reserva.getCliente().getEmpresa().getRuc();

        String celular = reserva.getCliente().getPersona() != null
                ? reserva.getCliente().getPersona().getCelular()
                : reserva.getCliente().getEmpresa().getTelefono();

        String numero = null;
        String serie = null;

        switch (reserva.getTipoComprobante()) {
            case 'B':
                Boleta boleta = boletaRepository.findByReserva(reserva).orElse(null);
                if (boleta != null) {
                    numero = boleta.getNumero();
                    serie = boleta.getSerie();
                }
                break;
            case 'F':
                Factura factura = facturaRepository.findByReserva(reserva).orElse(null);
                if (factura != null) {
                    numero = factura.getNumero();
                    serie = factura.getSerie();
                }
                break;
            case 'T':
                Ticket ticket = ticketRepository.findByReserva(reserva).orElse(null);
                if (ticket != null) {
                    numero = ticket.getNumero();
                    serie = ticket.getSerie();
                }
                break;
            default:
                throw new RuntimeException("Invalid comprobante type");
        }

        List<DetalleVenta> detallesVenta = detalleVentaRepository.findByVenta(reserva);
        List<DetalleVentaDTO> detalleVentaDTOs = detallesVenta.stream()
                .map(detalle -> DetalleVentaDTO.builder()
                        .campoId(detalle.getCampo().getId())
                        .campoNombre(detalle.getCampo().getNombre())
                        .precio(detalle.getCampo().getPrecio())
                        .horarioId(detalle.getHorario().getId())
                        .horaInicio(detalle.getHorario().getHoraInicio())
                        .horaFinal(detalle.getHorario().getHoraFinal())
                        .build())
                .collect(Collectors.toList());

        Campo campo = detallesVenta.isEmpty() ? null : detallesVenta.get(0).getCampo();
        Compania compania = campo != null && campo.getUsuario() != null && campo.getUsuario().getSede() != null
                ? campo.getUsuario().getSede().getSucursal().getCompania()
                : null;

        Imagen imagen = compania != null ? compania.getImagen() : null;

        return ReservaResponseDTO.builder()
                .reservaId(reserva.getId())
                .cliente(cliente)
                .direccionCliente(direccionCliente)
                .identificacion(identificacion)
                .celular(celular)
                .comprobante(getComprobanteType(reserva.getTipoComprobante()))
                .igv(reserva.getIgv())
                .descuento(reserva.getDescuento())
                .fecha(reserva.getFecha())
                .subtotal(reserva.getSubtotal())
                .total(reserva.getTotal())
                .campo(campo != null ? campo.getNombre() : null)
                .precio(campo != null ? campo.getPrecio() : null)
                .numero(numero)
                .serie(serie)
                .razonSocial(compania != null ? compania.getEmpresa().getRazonSocial() : null)
                .ruc(compania != null ? compania.getEmpresa().getRuc() : null)
                .telefonoEmpresa(compania != null ? compania.getEmpresa().getTelefono() : null)
                .direccionEmpresa(compania != null ? compania.getEmpresa().getDireccion() : null)
                .concepto(compania != null ? compania.getConcepto() : null)
                .imageUrl(imagen != null ? imagen.getImageUrl() : null)
                .sucursalNombre(campo != null && campo.getUsuario().getSede() != null
                        ? campo.getUsuario().getSede().getSucursal().getNombre() : null)
                .paginaWeb(compania != null ? compania.getPagWeb() : null)
                .sedeNombre(campo != null && campo.getUsuario().getSede() != null
                        ? campo.getUsuario().getSede().getNombre() : null)
                .detallesVenta(detalleVentaDTOs)
                .build();
    }


    private String getComprobanteType(Character tipoComprobante) {
        return switch (tipoComprobante) {
            case 'B' -> "BOLETA";
            case 'F' -> "FACTURA";
            default -> "TICKET";
        };
    }

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
    public int getTotalReservas() {
        return (int) reservaRepository.count();
    }


}
