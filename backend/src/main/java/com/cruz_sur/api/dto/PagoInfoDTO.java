package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoInfoDTO {
    private String celular;
    private String qrImagenUrl;  // URL del QR
}
