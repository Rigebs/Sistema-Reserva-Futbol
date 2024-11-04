package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampoAvailabilityService {
    private final DetalleVentaRepository detalleVentaRepository;

    public boolean isCampoAvailable(Long campoId, LocalDate fecha, Time horaInicio, Time horaFin) {
        if (horaFin.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return false;
        }

        List<DetalleVenta> reservas = detalleVentaRepository.findByCampoIdAndVenta_Fecha(campoId, fecha);
        for (DetalleVenta reserva : reservas) {
            if (reserva.getHoraInicio().before(horaFin) && reserva.getHoraFinal().after(horaInicio)) {
                return false;
            }
        }
        return true;
    }

}
