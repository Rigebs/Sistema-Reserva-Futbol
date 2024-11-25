package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaCompaniaDTO {
    private Long idCompania;
    private String empresaNombre;
    private String empresaRuc;
    private String empresaDireccion;
    private String companiaNombre;
    private String companiaCorreo;
    private String companiaCelular;
    private String companiaImagenUrl;
    private String companiaQrImagenUrl;
}
