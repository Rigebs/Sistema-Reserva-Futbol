package com.cruz_sur.api.model.dto;

import lombok.Data;

@Data
public class UsuarioRegistrationDto {
    private String logeo;
    private String clave;
    private String email;
    private Long rolId; // Optional, since it will be overridden by the logic
    private Long clienteId; // Optional
    private Long companiaId; // New field
    private String usuarioCreacion;
}
