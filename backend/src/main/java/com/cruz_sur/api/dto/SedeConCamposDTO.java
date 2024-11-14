package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SedeConCamposDTO {
    private Long userId;
    private Long companiaId;
    private String companiaNombre;
    private String companiaImagenUrl;
    private String direccion;
    private List<CampoDTO> camposWithSede; // Lista de campos asociados a la sede
}
