package com.cruz_sur.api.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
public class ReservaDTO {
    private LocalDate fecha;
    private BigDecimal descuento;
    private BigDecimal igv;
    private Character tipoComprobante;
    private Long metodoPagoId;
    private Long horarioId;
}
