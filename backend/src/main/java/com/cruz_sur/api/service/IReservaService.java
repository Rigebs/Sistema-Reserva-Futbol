package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.*;

import java.util.List;

public interface IReservaService {
    ReservaResponseDTO createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta);

    List<VentaDTO> getVentasByUsuario();
    int getTotalReservas(); // New method for total reservations

}
