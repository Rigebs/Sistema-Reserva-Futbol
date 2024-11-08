package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CampoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private Character estado;
    private Long usuarioId;
    private Long tipoDeporteId;
}
