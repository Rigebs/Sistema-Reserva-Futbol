package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailResponseDto {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;

    private String sedeName;
    private String sucursalName;

    private String companiaName;
    private String companiaImageUrl;

    private String empresaRuc;
    private String empresaRazonSocial;

    private String distritoNombre;
    private String provinciaNombre;
    private String departamentoNombre;
}
