package com.cruz_sur.api.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamposHomeDTO {
    private Long userId;
    private String companiaNombre;
    private String companiaImagenUrl;
    private String direccion;
}
