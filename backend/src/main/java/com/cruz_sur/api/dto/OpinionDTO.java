package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpinionDTO {
    private Long id;
    private String contenido;
    private Integer calificacion;
    private String usuarioCreacion;
    private LocalDateTime fechaCreacion;
    private Long userId;     // ID del usuario que realiza la opinión
    private Long companiaId; // ID de la compañía
}
