package com.cruz_sur.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Time;

@Data
@Builder
@ToString
public class DetalleVentaDTO {
    private Long campoId;
    private String campoNombre;
    private BigDecimal precio;

    private Time horaInicio; // Start time of Horario
    private Time horaFinal;  // End time of Horario
}
