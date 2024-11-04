package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.dto.ReservaResponseDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.BoletaRepository;
import com.cruz_sur.api.repository.FacturaRepository;
import com.cruz_sur.api.repository.TicketRepository;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservaResponseBuilder {

    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;
    private final TicketRepository ticketRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public ReservaResponseBuilder(BoletaRepository boletaRepository, FacturaRepository facturaRepository, TicketRepository ticketRepository, DetalleVentaRepository detalleVentaRepository) {
        this.boletaRepository = boletaRepository;
        this.facturaRepository = facturaRepository;
        this.ticketRepository = ticketRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    public ReservaResponseDTO build(Reserva reserva) {
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
                        // Removed horarioId from here
                        .horaInicio(detalle.getHoraInicio())
                        .horaFinal(detalle.getHoraFinal())
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
}
