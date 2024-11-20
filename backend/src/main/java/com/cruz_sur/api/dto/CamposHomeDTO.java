package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
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
    private List<String> tipoDeporteNombre; // Lista de tipos de deportes asociados a la sede
}
