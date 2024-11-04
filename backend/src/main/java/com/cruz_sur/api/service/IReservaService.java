package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.*;
import com.cruz_sur.api.responses.TotalReservasResponse;

import java.util.List;

public interface IReservaService {
    ReservaResponseDTO createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta);

    List<VentaDTO> getVentasByUsuario();
    TotalReservasResponse getTotalReservas();

    TotalReservasResponse getTotalReservasSede();
    List<ReservaDisplayDTO> getReservasForLoggedUser();
}
