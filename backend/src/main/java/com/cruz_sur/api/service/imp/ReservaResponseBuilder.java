package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.dto.ReservaResponseDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.BoletaRepository;
import com.cruz_sur.api.repository.FacturaRepository;
import com.cruz_sur.api.repository.TicketRepository;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReservaResponseBuilder {

    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;
    private final TicketRepository ticketRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public ReservaResponseDTO build(Reserva reserva) {
        // Obtener datos del cliente
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

        // Obtener el número y serie del comprobante según el tipo
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

        // Detalles de venta
        List<DetalleVenta> detallesVenta = detalleVentaRepository.findByVenta(reserva);
        List<DetalleVentaDTO> detalleVentaDTOs = detallesVenta.stream()
                .map(detalle -> DetalleVentaDTO.builder()
                        .campoId(detalle.getCampo().getId())
                        .campoNombre(detalle.getCampo().getNombre())
                        .precio(detalle.getPrecio()) // Aquí tomamos el precio del detalle de venta
                        .horaInicio(detalle.getHoraInicio())
                        .horaFinal(detalle.getHoraFinal())
                        .build())
                .collect(Collectors.toList());

        // Obtenemos los datos de la compañía a partir del campo
        Compania compania = detallesVenta.isEmpty() ? null : detallesVenta.get(0).getCampo().getUsuario().getSede();

        // Obtenemos la imagen de la compañía
        Imagen imagen = compania != null ? compania.getImagen() : null;

        // Construcción del DTO de respuesta
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
                .fechaCreacion(reserva.getFechaCreacion())
                .subtotal(reserva.getSubtotal())
                .total(reserva.getTotal())
                .numero(numero)
                .serie(serie)
                .razonSocial(compania != null ? compania.getEmpresa().getRazonSocial() : null)
                .ruc(compania != null ? compania.getEmpresa().getRuc() : null)
                .telefonoEmpresa(compania != null ? compania.getEmpresa().getTelefono() : null)
                .direccionEmpresa(compania != null ? compania.getEmpresa().getDireccion() : null)
                .concepto(compania != null ? compania.getConcepto() : null)
                .imageUrl(imagen != null ? imagen.getImageUrl() : null)
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
}
