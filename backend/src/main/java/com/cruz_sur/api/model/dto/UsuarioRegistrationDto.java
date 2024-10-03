package com.cruz_sur.api.model.dto;

import lombok.Data;

@Data
public class UsuarioRegistrationDto {
    private String logeo;
    private String clave;
    private String email;
    private Long rolId;
    private Long clienteId;
    private String usuarioCreacion;
}
