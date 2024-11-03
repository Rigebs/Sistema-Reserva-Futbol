package com.cruz_sur.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaDTO {
    private Long reservaId;
    private LocalDate fecha;
    private BigDecimal total;
    private String tipoComprobante;
    private String estado; // Optionally, add more fields as needed

    // Getters and Setters
}
