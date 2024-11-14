package com.cruz_sur.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VentasMensualesDTO {
    private Integer anio;
    private Long companiaId;
    private String companiaNombre;
    private BigDecimal enero;
    private BigDecimal febrero;
    private BigDecimal marzo;
    private BigDecimal abril;
    private BigDecimal mayo;
    private BigDecimal junio;
    private BigDecimal julio;
    private BigDecimal agosto;
    private BigDecimal septiembre;
    private BigDecimal octubre;
    private BigDecimal noviembre;
    private BigDecimal diciembre;
    private BigDecimal totalHastaHoy;

}
