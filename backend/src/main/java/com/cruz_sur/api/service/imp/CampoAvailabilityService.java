package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampoAvailabilityService {
    private final DetalleVentaRepository detalleVentaRepository;

    // Existing method to check if a specific time range is available
    public boolean isCampoAvailable(Long campoId, LocalDate fecha, Time horaInicio, Time horaFin) {
        if (horaFin.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return false;
        }

        List<DetalleVenta> reservas = detalleVentaRepository.findByCampoIdAndVenta_Fecha(campoId, fecha)
                .stream()
                .filter(detalle -> detalle.getVenta().getEstado() == '1')
                .toList();

        for (DetalleVenta reserva : reservas) {
            if (reserva.getHoraInicio().before(horaFin) && reserva.getHoraFinal().after(horaInicio)) {
                return false;
            }
        }
        return true;
    }

    // Updated method to get available hours
    public List<Time> getAvailableHours(Long campoId, LocalDate fecha, List<Time> allHours) {
        List<DetalleVenta> reservas = detalleVentaRepository.findByCampoIdAndVenta_Fecha(campoId, fecha);
        List<Time> availableHours = new ArrayList<>(allHours);

        for (DetalleVenta reserva : reservas) {
            // Remove hours that are either within the reserved time or equal to the reserved times
            availableHours.removeIf(hour ->
                    !hour.before(reserva.getHoraInicio()) && !hour.after(reserva.getHoraFinal()));
        }

        return availableHours;
    }
}
