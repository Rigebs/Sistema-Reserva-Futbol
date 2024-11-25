package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.responses.TotalReservasResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface IReservaService {
    ReservaResponseDTO createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta);

    @Transactional
    ReservaResponseDTO validarPagoReserva(Long reservaId, BigDecimal montoPago);
    boolean isReservaActive(Long reservaId);

    List<VentaDTO> getVentasByUsuario();
    TotalReservasResponse getTotalReservas();

    TotalReservasResponse getTotalReservasSede();
    List<ReservaDisplayDTO> getReservasForLoggedUser();
}
