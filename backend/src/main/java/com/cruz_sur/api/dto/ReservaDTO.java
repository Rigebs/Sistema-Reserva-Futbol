package com.cruz_sur.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private LocalDateTime fecha;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;
    private Character tipoComprobante;
    private Long metodoPagoId;
    private Long horarioId;
}
