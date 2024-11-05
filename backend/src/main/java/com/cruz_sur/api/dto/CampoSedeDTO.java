package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CampoSedeDTO {
    private Long usuarioId;
    private String sedeNombre;
    private String sucursalNombre;
    private String companiaNombre;
    private String companiaCorreo;
    private String companiaPagWeb;
    private String companiaImageUrl;
}
