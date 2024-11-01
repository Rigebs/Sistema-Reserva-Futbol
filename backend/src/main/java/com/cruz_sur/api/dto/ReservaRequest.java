package com.cruz_sur.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReservaRequest {
    private ReservaDTO reservaDTO;
    private List<DetalleVentaDTO> detallesVenta;
}
