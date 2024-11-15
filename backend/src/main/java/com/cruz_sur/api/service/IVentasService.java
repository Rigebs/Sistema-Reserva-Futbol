package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.ContarReservasDTO;
import com.cruz_sur.api.dto.VentasMensualesDTO;

import java.util.List;

public interface IVentasService {
    List<VentasMensualesDTO> all();

    ContarReservasDTO countTotalReservas(String fecha);

    List<ContarReservasDTO> countCamposReservados();

    ContarReservasDTO countTotalReservasDiarias(String fecha);
}
