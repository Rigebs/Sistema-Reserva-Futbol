package com.cruz_sur.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {
    private Long campoId;
    private String campoNombre;
    private BigDecimal precio;

}
