package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.dto.ReservaDTO;
import com.cruz_sur.api.model.Reserva;

import java.util.List;

public interface IReservaService {
    Reserva createReserva(ReservaDTO reservaDTO, List<DetalleVentaDTO> detallesVenta);

}
