package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;  // Import LocalTime
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long reservaId;
    private String cliente;
    private String direccionCliente;
    private String identificacion;
    private String celular;
    private String comprobante;
    private BigDecimal igv;
    private BigDecimal descuento;
    private LocalDate fecha;
    private LocalDateTime fechaCreacion;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String campo;
    private BigDecimal precio;
    private String numero;
    private String serie;
    private String razonSocial;
    private String ruc;
    private String telefonoEmpresa;
    private String direccionEmpresa;
    private String concepto;
    private String imageUrl;
    private String sucursalNombre;
    private String sedeNombre;
    private List<DetalleVentaDTO> detallesVenta; // Existing field
}
