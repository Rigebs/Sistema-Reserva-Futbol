package com.cruz_sur.api.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamposHomeDTO {
    private Long userId;
    private Long companiaId;
    private String companiaNombre;
    private String companiaImagenUrl;
    private String direccion;
    private List<String> tipoDeporteNombre;
    private List<CampoDTO> camposWithSede;
}
